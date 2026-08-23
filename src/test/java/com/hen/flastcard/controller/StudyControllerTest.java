package com.hen.flastcard.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.hen.flastcard.dto.request.ReviewRequest;
import com.hen.flastcard.dto.response.ReviewResponse;
import com.hen.flastcard.dto.response.StudyCardResponse;
import com.hen.flastcard.service.StudyService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(StudyController.class)
class StudyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StudyService studyService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMappingContext;

    private StudyCardResponse studyCardResponse;
    private ReviewRequest reviewRequest;
    private ReviewResponse reviewResponse;

    @BeforeEach
    void initData() {
        studyCardResponse = StudyCardResponse.builder()
                .cardId(1L)
                .word("こんにちは")
                .hira_kata("こんにちは")
                .meaning("Xin chào")
                .example("こんにちは、お元気ですか。")
                .build();

        reviewRequest = ReviewRequest.builder().cardId(1L).quality(5).build();

        reviewResponse = ReviewResponse.builder()
                .success(true)
                .nextReviewDate(LocalDateTime.of(2026, 8, 25, 10, 0))
                .build();
    }

    @Test
    void startStudying_success() throws Exception {
        when(studyService.loadStudyCards(1L)).thenReturn(List.of(studyCardResponse));

        mockMvc.perform(get("/api/study/decks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result").isArray())
                .andExpect(jsonPath("$.result.length()").value(1))
                .andExpect(jsonPath("$.result[0].cardId").value(1))
                .andExpect(jsonPath("$.result[0].word").value("こんにちは"))
                .andExpect(jsonPath("$.result[0].hira_kata").value("こんにちは"))
                .andExpect(jsonPath("$.result[0].meaning").value("Xin chào"))
                .andExpect(jsonPath("$.result[0].example").value("こんにちは、お元気ですか。"));

        verify(studyService).loadStudyCards(1L);
    }

    @Test
    void reviewCard_success() throws Exception {
        when(studyService.reviewCard(reviewRequest)).thenReturn(reviewResponse);

        mockMvc.perform(post("/api/study/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.success").value(true))
                .andExpect(jsonPath("$.result.nextReviewDate").value("2026-08-25T10:00:00"));

        verify(studyService).reviewCard(reviewRequest);
    }
}
