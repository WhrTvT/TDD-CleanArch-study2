package io.hhplus.cleanarch.domain.lectureHistory;

import io.hhplus.cleanarch.domain.lecture.Lecture;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter @Setter
@NoArgsConstructor
public class LectureHistory {

    // 기록 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    // Lecture의 lectureId를 조인
    @ManyToOne // N:1
    @JoinColumn(name = "lectureId")
    private Lecture lecture;

    // 유저 ID
    private Long userId;
    // 기록 날짜
    private Timestamp historyDate;
}
