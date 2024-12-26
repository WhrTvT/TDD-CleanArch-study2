package io.hhplus.cleanarch.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LectureCapacity {
    CAPACITY(30); // 특강 최대 인원

    private final int max;
}
