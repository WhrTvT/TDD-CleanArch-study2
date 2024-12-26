package io.hhplus.cleanarch.infrastructure.lecture;

import io.hhplus.cleanarch.domain.lecture.Lecture;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LectureJpaRepository extends JpaRepository<Lecture, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Lecture findByLectureId(Long lectureId);

    @Query("SELECT l FROM Lecture l WHERE l.lectureId = :lectureId")
    Lecture findByLectureIdWithoutLock(@Param("lectureId") Long lectureId);

    // SELECT * FORM lecture WHERE lecture_at = ? AND lecture_capacity < ?
    List<Lecture> findByLectureAtAndCapacityCurrentLessThan(LocalDate lectureAt, long capacityCurrent);
}
