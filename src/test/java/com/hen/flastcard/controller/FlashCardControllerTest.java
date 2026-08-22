package com.hen.flastcard.controller;

import com.hen.flastcard.dto.request.FlashCardRequest;
import com.hen.flastcard.dto.response.FlashCardResponse;
import com.hen.flastcard.service.FlashCardService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(FlashCardController.class)
class FlashCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FlashCardService flashCardService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMappingContext;

    private FlashCardRequest flashCardRequest;
    private FlashCardResponse flashCardResponse;
    private List<FlashCardResponse> flashCardResponses;

    @BeforeEach
    void initData() {
        flashCardRequest = FlashCardRequest.builder()
                .word("こんにちは")
                .hira_kata("こんにちは")
                .meaning("Xin chào")
                .example("こんにちは、お元気ですか。")
                .build();

        flashCardResponse = FlashCardResponse.builder()
                .id(1L)
                .word("こんにちは")
                .hira_kata("こんにちは")
                .meaning("Xin chào")
                .example("こんにちは、お元気ですか。")
                .build();

        flashCardResponses = List.of(
                flashCardResponse,
                FlashCardResponse.builder()
                        .id(2L)
                        .word("ありがとう")
                        .hira_kata("ありがとう")
                        .meaning("Cảm ơn")
                        .example("ありがとうございます。")
                        .build()
        );
    }

    @Test
    void createFlashCard_validRequest_success() throws Exception {
        when(flashCardService.createFlashCard(1L, flashCardRequest))
                .thenReturn(flashCardResponse);

        mockMvc.perform(
                        post("/api/decks/1/cards")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(flashCardRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.id").value(1))
                .andExpect(jsonPath("$.result.word").value("こんにちは"))
                .andExpect(jsonPath("$.result.hira_kata").value("こんにちは"))
                .andExpect(jsonPath("$.result.meaning").value("Xin chào"))
                .andExpect(jsonPath("$.result.example")
                        .value("こんにちは、お元気ですか。"));

        verify(flashCardService).createFlashCard(1L, flashCardRequest);
    }

    @Test
    void updateFlashCard_validRequest_success() throws Exception {
        when(flashCardService.updateFlashCard(1L, flashCardRequest))
                .thenReturn(flashCardResponse);

        mockMvc.perform(
                        put("/api/cards/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(flashCardRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.id").value(1))
                .andExpect(jsonPath("$.result.word").value("こんにちは"))
                .andExpect(jsonPath("$.result.meaning").value("Xin chào"));

        verify(flashCardService).updateFlashCard(1L, flashCardRequest);
    }

    @Test
    void getAllCards_success() throws Exception {
        when(flashCardService.getAll(1L))
                .thenReturn(flashCardResponses);

        mockMvc.perform(get("/api/decks/1/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result").isArray())
                .andExpect(jsonPath("$.result.length()").value(2))
                .andExpect(jsonPath("$.result[0].id").value(1))
                .andExpect(jsonPath("$.result[0].word").value("こんにちは"))
                .andExpect(jsonPath("$.result[1].id").value(2))
                .andExpect(jsonPath("$.result[1].word").value("ありがとう"));

        verify(flashCardService).getAll(1L);
    }

    @Test
    void deleteCard_success() throws Exception {
        when(flashCardService.deleteFlashCard(1L))
                .thenReturn("FlashCard has been deleted");

        mockMvc.perform(delete("/api/cards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result")
                        .value("FlashCard has been deleted"));

        verify(flashCardService).deleteFlashCard(1L);
    }
}