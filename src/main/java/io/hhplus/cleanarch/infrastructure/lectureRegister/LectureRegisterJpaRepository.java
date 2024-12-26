package io.hhplus.cleanarch.infrastructure.lectureRegister;

import io.hhplus.cleanarch.domain.lectureRegister.LectureRegister;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LectureRegisterJpaRepository extends JpaRepository<LectureRegister, Long> {
    List<LectureRegister> findAllByUserId(long userId);

    Optional<LectureRegister> findByUserIdAndLectureId(long userId, long lectureId);
}
