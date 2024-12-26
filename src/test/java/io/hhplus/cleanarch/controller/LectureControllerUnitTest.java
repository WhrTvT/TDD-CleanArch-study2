package io.hhplus.cleanarch.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.hhplus.cleanarch.domain.LectureService;
import io.hhplus.cleanarch.domain.lecture.Lecture;
import io.hhplus.cleanarch.domain.lectureRegister.LectureRegister;
import io.hhplus.cleanarch.interfaces.lecture.LectureController;
import io.hhplus.cleanarch.interfaces.lecture.LectureRegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
class LectureControllerUnitTest {
    /**
     * LectureController 단위 테스트
     * 1. 특강 조회 -> 정원이 찬 특강은 조회 제외
     * 2. 특강 신청 -> 신청하는 특강이 유효한 특강이 아니면 실패
     *            -> 30명이 초과되면 이후 신청자의 요청은 실패
     *            -> 한 유저가 동일한 특강을 여러 번 신청하면 1번만 성공, 나머지 실패
     * 3. 특강 신청 조회 -> 각 특강의 ID, TITLE, Lecturer 정보 출력 필수
     */

    MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    private LectureService lectureService;

    @InjectMocks
    private LectureController lectureController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(lectureController).build();
    }

    LocalDateTime registerAt = LocalDateTime.now();
    LocalDate lectureAt = LocalDate.now().plusDays(1);

    @Test
    @DisplayName("특강 조회")
    void availableLectures() throws Exception {
        // Given
        Lecture lecture1 = Lecture.builder()
                .lectureId(1L)
                .title("말하는 감자 탈출")
                .lecturer("장수현")
                .capacityCurrent(28)
                .lectureAt(lectureAt)
                .build();

        Lecture lecture2 = Lecture.builder()
                .lectureId(2L)
                .title("멍때리기 대회 1등 비법")
                .lecturer("22팀")
                .capacityCurrent(29)
                .lectureAt(lectureAt)
                .build();

        Lecture lecture3 = Lecture.builder()
                .lectureId(3L)
                .title("뫼비우스의 테스트 작성 벗어나기")
                .lecturer("대가는 크리스마스")
                .capacityCurrent(30)
                .lectureAt(lectureAt)
                .build();

        when(lectureService.getAvailableLectures(lectureAt)).thenReturn(List.of(lecture1, lecture2));

        // When & Then
        mockMvc.perform(get("/api/lecture/{lectureAt}", lectureAt)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("말하는 감자 탈출"))
                .andExpect(jsonPath("$[1].title").value("멍때리기 대회 1등 비법"));

        verify(lectureService, times(1)).getAvailableLectures(lectureAt);
    }

    @Test
    @DisplayName("특강 신청")
    void registerLecture() throws Exception {
        // Given
        long lectureId = 1L;
        long userId = 1L;
        LectureRegisterRequest lectureRegisterRequest = new LectureRegisterRequest(userId, registerAt);

        LectureRegister lectureRegister = LectureRegister.builder()
                .lectureId(lectureId)
                .userId(userId)
                .registerAt(registerAt)
                .build();

        when(lectureService.setRegisterLecture(any())).thenReturn(lectureRegister);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String expectedFormattedDate = registerAt.format(formatter);

        // When & Then
        mockMvc.perform(patch("/api/lecture/register/{lectureId}", lectureId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lectureRegisterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lectureId").value(lectureId))
                .andExpect(jsonPath("$.registerAt").value(expectedFormattedDate));

        /*
        MvcResult result = mockMvc.perform(patch("/api/lecture/register/{lectureId}", lectureId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lectureRegisterRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        System.out.println("JSON Response: " + jsonResponse);
         */
    }

    @Test
    @DisplayName("특강 신청 완료 목록 조회")
    void registerLectureList() throws Exception {
        // Given
        long userId = 1L;

        Lecture lecture1 = Lecture.builder()
                .lectureId(1L)
                .title("말하는 감자 탈출")
                .lecturer("장수현")
                .capacityCurrent(28)
                .lectureAt(lectureAt)
                .build();

        Lecture lecture2 = Lecture.builder()
                .lectureId(2L)
                .title("멍때리기 대회 1등 비법")
                .lecturer("22팀")
                .capacityCurrent(29)
                .lectureAt(lectureAt)
                .build();

        when(lectureService.getRegisterLectureList(userId)).thenReturn(List.of(lecture1, lecture2));

        // When & Then
        mockMvc.perform(get("/api/lecture/list/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lectureId").value(lecture1.getLectureId()))
                .andExpect(jsonPath("$[0].title").value(lecture1.getTitle()))
                .andExpect(jsonPath("$[0].lecturer").value(lecture1.getLecturer()))
                .andExpect(jsonPath("$[1].lectureId").value(lecture2.getLectureId()))
                .andExpect(jsonPath("$[1].title").value(lecture2.getTitle()))
                .andExpect(jsonPath("$[1].lecturer").value(lecture2.getLecturer()));

        verify(lectureService, times(1)).getRegisterLectureList(userId);
    }
}