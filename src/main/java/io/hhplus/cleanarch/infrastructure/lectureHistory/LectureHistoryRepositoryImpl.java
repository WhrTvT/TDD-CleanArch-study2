package io.hhplus.cleanarch.infrastructure.lectureHistory;

import io.hhplus.cleanarch.domain.lectureHistory.LectureHistory;
import io.hhplus.cleanarch.domain.lectureHistory.LectureHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LectureHistoryRepositoryImpl implements LectureHistoryRepository {
    private final LectureHistoryJpaRepository lectureHistoryJpaRepository;


    @Override
    public Optional<LectureHistory> findByUserId(Long userId) {
        return lectureHistoryJpaRepository.findByUserId(userId);
    }

    @Override
    public LectureHistory save(LectureHistory lectureHistory) {
        return lectureHistoryJpaRepository.save(lectureHistory);
    }
}
