package io.hhplus.cleanarch.infrastructure.lecture;

import io.hhplus.cleanarch.domain.lecture.Lecture;
import io.hhplus.cleanarch.domain.lecture.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LectureRepositoryImpl implements LectureRepository {
    private final LectureJpaRepository lectureJpaRepository;

    @Override
    public Optional<Lecture> findByName(String lectureName) {
        return lectureJpaRepository.findByName(lectureName);
    }

    @Override
    public Lecture save(Lecture lecture) {
        return lectureJpaRepository.save(lecture);
    }
}
