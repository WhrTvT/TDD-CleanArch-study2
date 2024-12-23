package io.hhplus.cleanarch.interfaces;

import io.hhplus.cleanarch.domain.lecture.Lecture;

import java.sql.Timestamp;

// record는 DTO를 위한 객체
// client로 응답할 데이터
public record LectureResponse(
        Long lectureId,
        String lectureName,
        Timestamp lectureDate
) {
    public static LectureResponse from(Lecture lecture) {
        return new LectureResponse(
                lecture.getLectureId(),
                lecture.getLectureName(),
                lecture.getLectureDate()
        );
    }
}
