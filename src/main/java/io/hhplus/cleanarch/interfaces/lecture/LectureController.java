package io.hhplus.cleanarch.interfaces.lecture;

import io.hhplus.cleanarch.domain.LectureService;
import io.hhplus.cleanarch.domain.lecture.Lecture;
import io.hhplus.cleanarch.domain.lectureRegister.LectureRegister;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/lecture")
@RequiredArgsConstructor
public class LectureController {

    private static final Logger log = LoggerFactory.getLogger(LectureController.class);
    private final LectureService lectureService;

    // 특강 신청 가능 목록 조회 API
    @GetMapping("/{lectureAt}")
    public ResponseEntity<List<LectureResponse>> availableLectures(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate lectureAt // 해당 날짜
    ) {
        List<Lecture> lectures = lectureService.getAvailableLectures(lectureAt);
        log.info("Available lectures : {}", lectures);

        return ResponseEntity.ok(LectureResponse.froms(lectures));
    }

    // 특강 신청 API
    @PatchMapping("/register/{lectureId}")
    public ResponseEntity<LectureRegisterResponse> registerLecture(
            @PathVariable long lectureId, // 신청할 특강 ID
            @RequestBody LectureRegisterRequest lectureRegisterRequest // 신청자 정보
    ) {
            LectureRegister lectureRegister = lectureService.setRegisterLecture(lectureRegisterRequest.to(lectureId)); //  특강 신청 서비스기 요구(Request)하는 값(to)을 던지고, 받음.
            log.info("Register lecture : {}", lectureRegister);

            return ResponseEntity.ok(LectureRegisterResponse.from(lectureRegister)); // 200 코드 시, 응답(Response) 값(from) return.
    }

    // 특강 신청 완료 목록 조회 API
    @GetMapping("/list/{userId}")
    public ResponseEntity<List<LectureResponse>> registerLectureList(
            @PathVariable long userId
    ){
        List<Lecture> lectures = lectureService.getRegisterLectureList(userId);
        log.info("Registered lectures : {}", lectures);

        return ResponseEntity.ok(LectureResponse.froms(lectures));
    }
}
