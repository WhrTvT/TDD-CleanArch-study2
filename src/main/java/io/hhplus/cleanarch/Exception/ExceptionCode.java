package io.hhplus.cleanarch.Exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter // Getter 자동 생성
@RequiredArgsConstructor // 생성자 자동 생성
public enum ExceptionCode {
    LECTURE_NOT_FOUND(400, "lecture not found"),
    LECTURE_ALREADY_REGISTERED(400, "lecture already registered"),
    LECTURE_MAX_REGISTERED(400, "lecture max registered");

    private final int code;
    private final String message;
}