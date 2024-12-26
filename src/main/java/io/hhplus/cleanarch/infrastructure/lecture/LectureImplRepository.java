package io.hhplus.cleanarch.infrastructure.lecture;

import io.hhplus.cleanarch.domain.LectureCapacity;
import io.hhplus.cleanarch.domain.lecture.Lecture;
import io.hhplus.cleanarch.domain.lecture.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LectureImplRepository implements LectureRepository {
    private final LectureJpaRepository lectureJpaRepository;

    @Override
    public List<Lecture> findAvailableLecturesByLectureAt(LocalDate lectureAt) {
        return lectureJpaRepository.findByLectureAtAndCapacityCurrentLessThan(
                lectureAt, LectureCapacity.CAPACITY.getMax()
        );
    }

    @Override
    public Lecture findByLectureId(long lectureId) {
        return lectureJpaRepository.findByLectureId(lectureId);
    }

    @Override
    public Lecture save(Lecture lecture) {
        return lectureJpaRepository.save(lecture);
    }
}