package io.hhplus.cleanarch.domain;

import io.hhplus.cleanarch.Exception.BusinessLogicException;
import io.hhplus.cleanarch.Exception.ExceptionCode;
import io.hhplus.cleanarch.domain.lecture.Lecture;
import io.hhplus.cleanarch.domain.lecture.LectureRepository;
import io.hhplus.cleanarch.domain.lectureRegister.LectureRegister;
import io.hhplus.cleanarch.domain.lectureRegister.LectureRegisterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor // final이나 @NonNull으로 선언된 필드만을 파라미터로 받는 생성자를 생성
public class LectureService {
    private final LectureRepository lectureRepository;
    private final LectureRegisterRepository lectureRegisterRepository;

    // 존재하지 않는 강의 Exception
    public void isLectureNotExisted(long lectureId){
        if (lectureRepository.findByLectureId(lectureId) == null){
            throw new BusinessLogicException(ExceptionCode.LECTURE_NOT_FOUND);
        }
    }

    // 강의 중복 신청 Exception
    public void isLectureAlreadyRegistered(long userId, long lectureId) {
        // userId와 lectureId로 LectureRegister 테이블에 동일한 항목이 존재하는지 확인
        if (lectureRegisterRepository.findByUserIdAndLectureId(userId, lectureId).isPresent()){
           throw new BusinessLogicException(ExceptionCode.LECTURE_ALREADY_REGISTERED);
        }
    }

    // 강의 정원 가득참 Exception
    public void isLectureMaxRegistered(long capacityCurrent){
        // lectuerId의 currentCount가 CAPACITY의 MAX(30)값을 넘는지 확인
        if (capacityCurrent >= (LectureCapacity.CAPACITY.getMax())){
            throw new BusinessLogicException(ExceptionCode.LECTURE_MAX_REGISTERED);
        }
    }

    /**
     * 특강 신청 가능 목록 서비스
     */
    @Transactional
    public List<Lecture> getAvailableLectures(
            LocalDate lectureAt
    ){
        // 해당 날짜의 특강 목록에서 CAPACITY(30) 미만의 것만 조회
        return lectureRepository.findAvailableLecturesByLectureAt(lectureAt);
    }

    /**
     * 특강 신청 서비스
     */
    @Transactional
    public LectureRegister setRegisterLecture(
            LectureRegister lectureRegister
    ){
        // lectureId로 특강 조회
        Lecture lecture = lectureRepository.findByLectureId(lectureRegister.getLectureId());

        // 특강 내역이 존재하는지 여부
        isLectureNotExisted(lecture.getLectureId());

        //동일한 특강 신청 내역이 있는지 여부
        isLectureAlreadyRegistered(lectureRegister.getUserId(), lectureRegister.getLectureId());

        //해당 특강의 정원(30)에 여유가 있는지 여부
        isLectureMaxRegistered(lecture.getCapacityCurrent());

        //특강 신청(저장)
        LectureRegister savedRegister = lectureRegisterRepository.save(lectureRegister);

        //특강 신청인원 증가
        lecture.incrementCurrentCount();

        return savedRegister;
    }

    /**
     * 특강 신청 완료 목록 조회 서비스
     */
    @Transactional
    public List<Lecture> getRegisterLectureList(
            long userId
    ){
        // userId로 특강 신청 완료 목록 조회
        List<LectureRegister> lectureRegister = lectureRegisterRepository.findAllByUserId(userId);

        /**
         * LectureRegister에 등록된 정보를 stream()으로 변환
         * map은 스트림 내의 각 요소를 변환(LectureRegister 객체에서 Lecture 객체를 추출)
         * 이후, toList()로 다시 리스트로 변환
         */
        return lectureRegister.stream().map(LectureRegister::getLecture).toList();
    }
}
