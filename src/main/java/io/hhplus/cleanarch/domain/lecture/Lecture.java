package io.hhplus.cleanarch.domain.lecture;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Lecture {

    // 강의 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lectureId;

    // 강의명
    private String lectureName;

    // 강의날짜
    private Timestamp lectureDate;
}
