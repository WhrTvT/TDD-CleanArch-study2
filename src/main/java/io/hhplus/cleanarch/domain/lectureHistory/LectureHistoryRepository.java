package io.hhplus.cleanarch.domain.lectureHistory;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LectureHistoryRepository {
    Optional<LectureHistory> findByUserId(Long userId);

    LectureHistory save(LectureHistory lectureHistory);
}
