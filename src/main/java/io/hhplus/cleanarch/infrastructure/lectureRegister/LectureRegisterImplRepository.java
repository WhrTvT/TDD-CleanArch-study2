package io.hhplus.cleanarch.infrastructure.lectureRegister;

import io.hhplus.cleanarch.domain.lectureRegister.LectureRegister;
import io.hhplus.cleanarch.domain.lectureRegister.LectureRegisterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LectureRegisterImplRepository implements LectureRegisterRepository {
    private final LectureRegisterJpaRepository lectureRegisterJpaRepository;


    @Override
    public List<LectureRegister> findAllByUserId(long userId) {
        return lectureRegisterJpaRepository.findAllByUserId(userId);
    }

    @Override
    public Optional<LectureRegister> findByUserIdAndLectureId(long userId, long lectureId) {
        return lectureRegisterJpaRepository.findByUserIdAndLectureId(
                userId, lectureId
        );
    }

    @Override
    public LectureRegister save(LectureRegister lectureRegister) {
        return lectureRegisterJpaRepository.save(lectureRegister);
    }
}
