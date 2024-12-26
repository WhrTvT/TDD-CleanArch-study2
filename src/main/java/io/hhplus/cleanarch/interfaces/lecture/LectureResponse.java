package io.hhplus.cleanarch.interfaces.lecture;

import io.hhplus.cleanarch.domain.lecture.Lecture;

import java.util.List;
import java.util.stream.Collectors;

// record는 DTO를 위한 객체
// client로 응답할 데이터
public record LectureResponse(
        long lectureId,
        String title,
        String lecturer
) {
    public static LectureResponse from(Lecture lecture) {
        return new LectureResponse(
                lecture.getLectureId(),
                lecture.getTitle(),
                lecture.getLecturer()
        );
    }

    public static List<LectureResponse> froms(List<Lecture> lectures) { // TODO - 이 부분의 로직이 이해되지 않음.
        return lectures.stream().map(LectureResponse::from).collect(Collectors.toList());
    }
}
