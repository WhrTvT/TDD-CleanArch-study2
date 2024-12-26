package io.hhplus.cleanarch.integration;

import io.hhplus.cleanarch.Exception.BusinessLogicException;
import io.hhplus.cleanarch.Exception.ExceptionCode;
import io.hhplus.cleanarch.domain.LectureService;
import io.hhplus.cleanarch.domain.lecture.Lecture;
import io.hhplus.cleanarch.domain.lecture.LectureRepository;
import io.hhplus.cleanarch.domain.lectureRegister.LectureRegister;
import io.hhplus.cleanarch.infrastructure.lecture.LectureJpaRepository;
import io.hhplus.cleanarch.infrastructure.lectureRegister.LectureRegisterJpaRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
class LectureServiceIntegrationTest {
    /**
     * LectureService 통합 테스트
     * 1. 특강 신청 가능 목록 서비스      -> 과거/현재 or 정원이 찬 특강은 선택하지 못한다
     * 2. 특강 신청 서비스              -> 30명이 초과되면 이후 신청자의 요청은 실패한다
     *                                  신청자가 같은 특강을 여러번 신청하면 한 번만 성공한다.
     * 3. 특강 신청 완료 목록 조회 서비스 -> 특정 userId로 신청된 특강 목록을 조회할 떄 특강의 ID, TITLE, Lecturer를 담고 있어야 한다.
     */

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private LectureJpaRepository lectureJpaRepository;

    @Autowired
    private LectureRegisterJpaRepository lectureRegisterJpaRepository;

    @Autowired
    private LectureService lectureService;

    LocalDateTime registerAt = LocalDateTime.now();
    LocalDate lectureAt = LocalDate.now().plusDays(1);

    @BeforeEach
    void setUp() {
        lectureRepository.save(Lecture.builder()
                .title("말하는 감자 탈출")
                .lecturer("장수현")
                .capacityCurrent(29)
                .lectureAt(lectureAt)
                .build());

        lectureRepository.save(Lecture.builder()
                .title("멍때리기 대회 1등 비법")
                .lecturer("22팀")
                .capacityCurrent(30) // 정원 참
                .lectureAt(lectureAt)
                .build());

    }

    @AfterEach
    void tearDown() {
        // 테스트 후 데이터 삭제
        lectureRegisterJpaRepository.deleteAll();
        lectureJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("특강 신청 가능 목록 서비스")
    void testAvailableLectures() {
        // Given

        // When
        List<Lecture> availableLectures = lectureService.getAvailableLectures(lectureAt);
        System.out.println("availableLectures : "+ availableLectures);

        // Then
        assertThat(availableLectures).hasSize(1);
        assertThat(availableLectures.get(0).getTitle()).isEqualTo("말하는 감자 탈출");
    }

    @Test
    @DisplayName("특강 신청 서비스 및 특강 신청 완료 목록 조회 서비스")
    void testRegisterLecture() {
        // Given
        List<Lecture> availableLectures = lectureService.getAvailableLectures(lectureAt);
        long lectureId = availableLectures.get(0).getLectureId(); // 가져온 lectureId
        System.out.println("availableLectures : "+ lectureId);

        LectureRegister  lectureRegister = LectureRegister.builder()
                .lectureId(lectureId)
                .userId(1L)
                .registerAt(registerAt)
                .build();

        // When
        LectureRegister savedRegister = lectureService.setRegisterLecture(lectureRegister);
        System.out.println("savedRegister : "+ savedRegister.getLectureId());

        List<Lecture> RegisterLectureList = lectureService.getRegisterLectureList(savedRegister.getUserId());
        System.out.println("RegisterLectureList : "+ RegisterLectureList.get(0).getLectureId());

        // Then
        assertThat(savedRegister).isNotNull();
        assertThat(savedRegister.getLectureId()).isEqualTo(lectureId);
        assertThat(savedRegister.getUserId()).isEqualTo(1L);

        assertThat(RegisterLectureList).isNotNull();
        assertThat(RegisterLectureList.get(0).getLectureId()).isEqualTo(lectureId);
        assertThat(RegisterLectureList.get(0).getTitle()).isEqualTo("말하는 감자 탈출");
        assertThat(RegisterLectureList.get(0).getLecturer()).isEqualTo("장수현");
        assertThat(RegisterLectureList.get(0).getCapacityCurrent()).isEqualTo(30);
    }

    @Test
    @DisplayName("동시에 동일한 특강에 대해 40명이 신청했을 때, 30명만 성공")
    void testUsersCapacityMaxRegisterLecture() throws InterruptedException {
        // Given
        int threadCount = 40;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount); // ThreadPool create
        CountDownLatch latch = new CountDownLatch(threadCount); // 40 users countdown

        List<LectureRegister> lectureRegisters = IntStream.range(0, threadCount) // userId0 ~ 39까지인 객체를 List로 수집
                .mapToObj(id -> LectureRegister.builder()
                        .lectureId(1L)
                        .userId(id)
                        .registerAt(registerAt)
                        .build())
                .toList();

        // When & Then
        lectureRegisters.forEach(lectureRegister -> executor.submit(() -> {
            try {
                if(lectureRegister.getUserId() < 30){ // 30번 성공
                    Assertions.assertThatCode(() -> lectureService.setRegisterLecture(lectureRegister))
                            .doesNotThrowAnyException();
                } else {
                    Assertions.assertThatThrownBy(() -> lectureService.setRegisterLecture(lectureRegister))
                            .isInstanceOf(BusinessLogicException.class)
                            .hasMessage(ExceptionCode.LECTURE_ALREADY_REGISTERED.getMessage());
                }

            } finally {
                latch.countDown();
            }
        }));

        latch.await();
        executor.shutdown();
    }

    @Test
    @DisplayName("동일한 유저 정보로 같은 특강을 5번 신청했을 때, 1번만 성공")
    void testUserDuplicatedRegisterLecture() {
        // Given
        LectureRegister lectureRegister = LectureRegister.builder()
                .lectureId(1L)
                .userId(1L)
                .registerAt(registerAt)
                .build();

        // When & Then
        Assertions.assertThatCode(() -> lectureService.setRegisterLecture(lectureRegister))
                .doesNotThrowAnyException(); // 첫 신청은 성공

        IntStream.range(0, 4).forEach(i -> Assertions.assertThatThrownBy(() -> lectureService.setRegisterLecture(lectureRegister))
                    .isInstanceOf(BusinessLogicException.class)
                    .hasMessage(ExceptionCode.LECTURE_ALREADY_REGISTERED.getMessage()));
    }
}