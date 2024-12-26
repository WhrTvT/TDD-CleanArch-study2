package io.hhplus.cleanarch.domain.lectureRegister;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LectureRegisterRepository {
    List<LectureRegister> findAllByUserId(long userId);

    Optional<LectureRegister> findByUserIdAndLectureId(long userId, long lectureId);

    LectureRegister save(LectureRegister lectureRegister);
}
