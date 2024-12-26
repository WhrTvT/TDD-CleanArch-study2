package io.hhplus.cleanarch.domain.lectureRegister;

import io.hhplus.cleanarch.domain.lecture.Lecture;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class LectureRegister {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long registerId; // 등록 ID

    private long lectureId; // INNER JOIN Lecture ON LectureRegister.lectureId = Lecture.lectureId
    private long userId; // 유저 ID
    private LocalDateTime registerAt; // 신청 날짜

    @CreatedDate
    private LocalDateTime createdAt; // 생성 날짜

    @LastModifiedDate
    private LocalDateTime updatedAt; // 업데이트 날짜

    @ManyToOne // N:1
    @JoinColumn(name = "lectureId", insertable=false, updatable=false) // Lecture의 lectureId를 조인
    // TODO - (insertable=false, updatable=false) error나서 붙임.
    private Lecture lecture;

    public LectureRegister(long lectureId, long userId, LocalDateTime registerAt) {
        this.lectureId = lectureId;
        this.userId = userId;
        this.registerAt = registerAt;
    }
}