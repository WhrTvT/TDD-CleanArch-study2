package io.hhplus.cleanarch.domain.lecture;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder // Builder를 사용하여 생성시 값을 지정하지 않으면 기본적으로는 타입에 따라 0, null, false 값이 할당된다.
@AllArgsConstructor // 모든 필드 값을 파라미터로 받는 생성자를 생성
@NoArgsConstructor // 파라미터가 없는 디폴트 생성자를 생성
@EntityListeners(AuditingEntityListener.class) // 시간에 대해서 자동으로 값을 넣어주는 기능
public class Lecture {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long lectureId; // 강의 ID
    private String title; // 강의명
    private String lecturer; // 강사
    private long capacityCurrent; // 강의 신청자수
    private LocalDate lectureAt; // 강의 날짜

    @CreatedDate
    private LocalDateTime createdAt; // 강의 생성 날짜

    @LastModifiedDate
    private LocalDateTime updatedAt; // 강의 수정 날짜

    public void incrementCurrentCount() {
        this.capacityCurrent++;
    }
}

// TODO - 해시코드, equals --> 찾아보기
/**
 * java.sql.Timestamp는 JDBC와 관련된 클래스이므로 데이터베이스 작업 외에 사용하기 적합하지 않습니다.
 * java.time.LocalDate은 명확하고 직관적이며 날짜 처리를 더 쉽게 지원합니다.
 */