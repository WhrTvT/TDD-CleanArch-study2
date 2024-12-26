package io.hhplus.cleanarch.domain;

import io.hhplus.cleanarch.Exception.BusinessLogicException;
import io.hhplus.cleanarch.Exception.ExceptionCode;
import io.hhplus.cleanarch.domain.lecture.Lecture;
import io.hhplus.cleanarch.domain.lecture.LectureRepository;
import io.hhplus.cleanarch.domain.lectureRegister.LectureRegister;
import io.hhplus.cleanarch.domain.lectureRegister.LectureRegisterRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

@ExtendWith(SpringExtension.class)
public class LectureServiceUnitTest {
    /**
     * LectureService 단위 테스트
     * 1. 특강 조회 -> 정원이 찬 특강은 조회 제외
     * 2. 특강 신청 -> 30명이 초과되면 이후 신청자의 요청은 실패
     * 3. 특강 신청 조회 -> X
     */

    @Mock
    private LectureRepository lectureRepository;
    @Mock
    private LectureRegisterRepository lectureRegisterRepository;

    private LectureService lectureService;

    LocalDate lectureAt = LocalDate.now().plusDays(1);
    LocalDateTime registerAt = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        lectureService = new LectureService(lectureRepository, lectureRegisterRepository);
    }

    @Test
    @DisplayName("정원이 찬 특강은 조회 불가")
    void testNotAvailableLectures() {
        // Given
        long capacityCurrent = LectureCapacity.CAPACITY.getMax(); // 30

        // When & Then
        Assertions.assertThatThrownBy(() -> lectureService.isLectureMaxRegistered(capacityCurrent))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessage(ExceptionCode.LECTURE_MAX_REGISTERED.getMessage());
    }

    @Test
    @DisplayName("30명이 초과되면 이후 신청자의 요청은 실패")
    void testLectureMaxRegistered() {
        // Given
        Lecture lecture = Lecture.builder()
                .lectureId(1L)
                .title("말하는 감자 탈출")
                .lecturer("장수현")
                .capacityCurrent(29)
                .lectureAt(lectureAt) // 미래 날짜
                .build();

        LectureRegister lectureRegister1 = LectureRegister.builder()
                .userId(1L)
                .lectureId(1L)
                .registerAt(registerAt)
                .build();

        // When & Then
        Assertions.assertThatThrownBy(() -> lectureService.isLectureNotExisted(lectureRegister1.getLectureId()))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessage(ExceptionCode.LECTURE_NOT_FOUND.getMessage()); // 강의 존재여부 예외 검증

        Assertions.assertThatCode(() -> lectureService.isLectureMaxRegistered(lecture.getCapacityCurrent()))
                .doesNotThrowAnyException(); // 강의 정원 가득 참 예외 검증

        lecture.incrementCurrentCount(); // 강의 신청자 증가

        Assertions.assertThatThrownBy(() -> lectureService.isLectureMaxRegistered(lecture.getCapacityCurrent()))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessage(ExceptionCode.LECTURE_MAX_REGISTERED.getMessage()); // 강의 정원 가득 참 예외 검증
    }
}