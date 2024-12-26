package io.hhplus.cleanarch.domain.lecture;

import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

// 여기서 JPArepo를 extends하면 DIP 위반
// https://velog.io/@namhm23/JPA-Repository-%EC%82%AC%EC%9A%A9-%EC%8B%9C-%EA%B3%84%EC%B8%B5%EA%B0%84-%EC%9D%98%EC%A1%B4%EA%B4%80%EA%B3%84-%EB%AC%B8%EC%A0%9C%EC%A0%90
@Repository
public interface LectureRepository{
    List<Lecture> findAvailableLecturesByLectureAt(LocalDate lectureAt);

    Lecture findByLectureId(long lectureId);

    Lecture save(Lecture lecture);
}
