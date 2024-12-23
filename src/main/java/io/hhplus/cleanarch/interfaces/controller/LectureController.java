package io.hhplus.cleanarch.interfaces.controller;

import io.hhplus.cleanarch.infrastructure.lecture.LectureRepositoryImpl;
import io.hhplus.cleanarch.infrastructure.lectureHistory.LectureHistoryRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lecture")
@RequiredArgsConstructor
public class LectureController {
    private final LectureRepositoryImpl lectureRepositoryImpl;
    private final LectureHistoryRepositoryImpl lectureHistoryRepositoryImpl;

    // TODO - 기능 구현
}
