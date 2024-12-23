package io.hhplus.cleanarch.infrastructure.lecture;

import io.hhplus.cleanarch.domain.lecture.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LectureJpaRepository extends JpaRepository<Lecture, Long> {

    Optional<Lecture> findByName(String lectureName);
}
