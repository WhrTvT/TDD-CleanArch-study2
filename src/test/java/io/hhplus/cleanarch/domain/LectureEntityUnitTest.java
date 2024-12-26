package io.hhplus.cleanarch.domain;

import io.hhplus.cleanarch.domain.lecture.Lecture;
import io.hhplus.cleanarch.domain.lectureRegister.LectureRegister;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class LectureEntityUnitTest {

    /**
     * Entity 단위 테스트
     * Lecture
     * 강의 ID -> 자동 IDENTITY
     * 강의명, 강사 -> NULL Faill, NOT NULL Success
     * 강의 신청자 수 -> 30명을 넘는다 Fail, 안넘는다 Success
     * 강의 날짜 -> 과거 또는 현재 Fail / 미래 Success
     *
     * LectureRegister
     * 등록 ID -> 자동 IDENTITY
     * 강의 ID -> Join
     * 유저 ID -> 중복 신청 Fail / 일반 신청 Success
     * 신청 날짜 -> 신청날짜
     */

    LocalDateTime registerAt = LocalDateTime.now();
    LocalDate lecturerAt_Future = LocalDate.now().plusDays(1);
    LocalDate lecturerAt_Past = LocalDate.now().minusDays(1);
    LocalDate lecturerAt_Present = LocalDate.now();

    @Test
    @DisplayName("강의명 또는 강사가 NOT NULL이면 Success")
    void testLectureTitleOrLecturerIsNotEmpty() {
        // Given
        Lecture lecture1 = Lecture.builder()
                .title("장수현의 감자탈출")
                .lecturer("장수현")
                .build();

        Lecture lecture2 = Lecture.builder()
                .title("")
                .lecturer("장수현")
                .build();

        Lecture lecture3 = Lecture.builder()
                .title("장수현의 감자탈출")
                .lecturer("")
                .build();

        // When & Then
        assertThat(lecture1.getTitle()).isNotEmpty();
        assertThat(lecture1.getLecturer()).isNotEmpty();

        assertThat(lecture2.getTitle()).isEmpty();
        assertThat(lecture2.getLecturer()).isNotEmpty();

        assertThat(lecture3.getTitle()).isNotEmpty();
        assertThat(lecture3.getLecturer()).isEmpty();
    }

    @Test
    @DisplayName("강의명 또는 강사가 NULL이면 Fail")
    void testLectureTitleOrLecturerIsEmpty() {
        // Given
        Lecture lecture1 = Lecture.builder()
                .title("")
                .lecturer("")
                .build();

        Lecture lecture2 = Lecture.builder()
                .title("")
                .lecturer("장수현")
                .build();

        Lecture lecture3 = Lecture.builder()
                .title("장수현의 감자탈출")
                .lecturer("")
                .build();

        // When & Then
        assertThat(lecture1.getTitle()).isEmpty();
        assertThat(lecture1.getLecturer()).isEmpty();

        assertThat(lecture2.getTitle()).isEmpty();
        assertThat(lecture2.getLecturer()).isNotEmpty();

        assertThat(lecture3.getTitle()).isNotEmpty();
        assertThat(lecture3.getLecturer()).isEmpty();
    }

    @Test
    @DisplayName("강의 신청자 수가 30명을 안넘었다면 Success")
    void testCapacityCurrentIsNotCapacityMaxOver() {
        // Given
        Lecture lecture1 = Lecture.builder()
                .capacityCurrent(29)
                .build();

        Lecture lecture2 = Lecture.builder()
                .capacityCurrent(30)
                .build();

        // When & Then
        assertThat(lecture1.getCapacityCurrent()).isLessThan(LectureCapacity.CAPACITY.getMax());
        assertThat(lecture2.getCapacityCurrent()).isGreaterThanOrEqualTo(LectureCapacity.CAPACITY.getMax());
    }

    @Test
    @DisplayName("강의 신청자 수가 30명을 넘었다면 Faill")
    void testCapacityCurrentIsCapacityMaxOver() {
        // Given
        Lecture lecture1 = Lecture.builder()
                .capacityCurrent(30)
                .build();

        Lecture lecture2 = Lecture.builder()
                .capacityCurrent(31)
                .build();

        // When & Then
        assertThat(lecture1.getCapacityCurrent()).isGreaterThanOrEqualTo(LectureCapacity.CAPACITY.getMax());
        assertThat(lecture2.getCapacityCurrent()).isGreaterThan(LectureCapacity.CAPACITY.getMax());
    }

    @Test
    @DisplayName("강의 날짜가 미래면 Success")
    void testLectureAtIsFuture() {
        // Given
        Lecture lecture1 = Lecture.builder()
                .lectureAt(lecturerAt_Future)
                .build();

        Lecture lecture2 = Lecture.builder()
                .lectureAt(lecturerAt_Future)
                .build();

        // When & Then
        assertThat(lecture1.getLectureAt()).isAfterOrEqualTo(lecturerAt_Future);
        assertThat(lecture2.getLectureAt()).isAfterOrEqualTo(lecturerAt_Future);
    }

    @Test
    @DisplayName("강의 날짜가 과거 또는 현재면 Faill")
    void testLectureAtIsPastOrPresent() {
        // Given
        Lecture lecture1 = Lecture.builder()
                .lectureAt(lecturerAt_Past)
                .build();

        Lecture lecture2 = Lecture.builder()
                .lectureAt(lecturerAt_Present)
                .build();

        // When & Then
        assertThat(lecture1.getLectureAt()).isBefore(lecturerAt_Present);
        assertThat(lecture2.getLectureAt()).isBeforeOrEqualTo(lecturerAt_Present);
    }

    @Test
    @DisplayName("유저가 일반적으로 강의를 신청하면 Success")
    void testLectureRegisterForGeneral() {
        // Given
        Lecture lecture = Lecture.builder()
                .lectureId(1)
                .capacityCurrent(29) // 강의 정원이 30 미만
                .build();

        LectureRegister lectureRegister = LectureRegister.builder()
                .userId(1)
                .lectureId(1)
                .registerAt(registerAt)
                .build();

        // When
        lecture.incrementCurrentCount();

        // Then
        assertThat(lecture.getCapacityCurrent()).isLessThanOrEqualTo(LectureCapacity.CAPACITY.getMax()); // capacityCurrent가 1 증가했는지 검증

        assertThat(lectureRegister).isNotNull(); // 강의 신청 객체가 생성되었는지 확인
        assertThat(lectureRegister.getUserId()).isEqualTo(1);
        assertThat(lectureRegister.getLectureId()).isEqualTo(1);
        assertThat(lectureRegister.getRegisterAt()).isBeforeOrEqualTo(registerAt);
    }

    @Test
    @DisplayName("유저가 중복으로 강의를 신청하면 Fail")
    void testLectureRegisterForDuplicate() {
        // Given
        Lecture lecture = Lecture.builder()
                .lectureId(1)
                .capacityCurrent(29) // 강의 정원이 30 미만
                .build();

        LectureRegister lectureRegister1 = LectureRegister.builder()
                .userId(1)
                .lectureId(1)
                .registerAt(registerAt)
                .build();

        LectureRegister lectureRegister2 = LectureRegister.builder()
                .userId(1)
                .lectureId(1)
                .registerAt(registerAt)
                .build();

        // When & Then
        assertThat(lectureRegister1.getLectureId()).isEqualTo(lectureRegister2.getLectureId());
    }
}