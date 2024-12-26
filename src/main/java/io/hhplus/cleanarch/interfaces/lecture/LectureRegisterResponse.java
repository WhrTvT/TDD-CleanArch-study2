package io.hhplus.cleanarch.interfaces.lecture;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.hhplus.cleanarch.domain.lectureRegister.LectureRegister;

import java.time.LocalDateTime;

// record는 DTO를 위한 객체
// client로 응답할 데이터
public record LectureRegisterResponse(
        long lectureId,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss") // 이거 안넣으면 [2024,12,26] 배열 형식으로 넘어옴.
        LocalDateTime registerAt
) {
    public static LectureRegisterResponse from(LectureRegister lectureRegister) {
        return new LectureRegisterResponse(
                lectureRegister.getLectureId(),
                lectureRegister.getRegisterAt()
        );
    }
}