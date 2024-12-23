package io.hhplus.cleanarch.infrastructure.lectureHistory;

import io.hhplus.cleanarch.domain.lectureHistory.LectureHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LectureHistoryJpaRepository extends JpaRepository<LectureHistory, Long> {

    Optional<LectureHistory> findByUserId(Long userId);
}
