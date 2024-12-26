package io.hhplus.cleanarch.interfaces.lecture;

import io.hhplus.cleanarch.domain.lectureRegister.LectureRegister;

import java.time.LocalDateTime;

// record는 DTO를 위한 객체
// client로부터 전달받을 데이터
public record LectureRegisterRequest(
        long userId,
        LocalDateTime registerAt
) {
    public LectureRegister to(long lectureId){
        return new LectureRegister(
                lectureId
                , this.userId
                , this.registerAt
        );
    }
}
