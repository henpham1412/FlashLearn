package com.hen.flastcard.controller;

import com.hen.flastcard.dto.request.DeckRequest;
import com.hen.flastcard.dto.response.DeckResponse;
import com.hen.flastcard.service.DeckService;

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


@WebMvcTest(DeckController.class)
class DeckControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DeckService deckService;

    // Fix @EnableJpaAuditing in FlastcardApplication
    @MockitoBean
    private JpaMetamodelMappingContext jpaMappingContext;

    private DeckRequest deckRequest;
    private DeckResponse deckResponse;
    private List<DeckResponse> deckResponses;

    @BeforeEach
    void initData() {
        deckRequest = DeckRequest.builder()
                .name("Japanese N5")
                .description("Japanese N5 vocabulary")
                .build();

        deckResponse = DeckResponse.builder()
                .id(1L)
                .name("Japanese N5")
                .description("Japanese N5 vocabulary")
                .build();

        deckResponses = List.of(
                deckResponse,
                DeckResponse.builder()
                        .id(2L)
                        .name("Japanese N4")
                        .description("Japanese N4 vocabulary")
                        .build()
        );
    }

    @Test
    void createDeck_validRequest_success() throws Exception {
        when(deckService.createDeck(deckRequest))
                .thenReturn(deckResponse);

        mockMvc.perform(
                        post("/api/decks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(deckRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.id").value(1))
                .andExpect(jsonPath("$.result.name").value("Japanese N5"))
                .andExpect(jsonPath("$.result.description")
                        .value("Japanese N5 vocabulary"));

        verify(deckService).createDeck(deckRequest);
    }

    @Test
    void getAll_success() throws Exception {
        when(deckService.getAll())
                .thenReturn(deckResponses);

        mockMvc.perform(get("/api/decks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result").isArray())
                .andExpect(jsonPath("$.result.length()").value(2))
                .andExpect(jsonPath("$.result[0].id").value(1))
                .andExpect(jsonPath("$.result[0].name").value("Japanese N5"))
                .andExpect(jsonPath("$.result[1].id").value(2))
                .andExpect(jsonPath("$.result[1].name").value("Japanese N4"));

        verify(deckService).getAll();
    }

    @Test
    void getById_success() throws Exception {
        when(deckService.getById(1L))
                .thenReturn(deckResponse);

        mockMvc.perform(get("/api/decks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.id").value(1))
                .andExpect(jsonPath("$.result.name").value("Japanese N5"))
                .andExpect(jsonPath("$.result.description")
                        .value("Japanese N5 vocabulary"));

        verify(deckService).getById(1L);
    }

    @Test
    void updateDeck_validRequest_success() throws Exception {
        when(deckService.updateDeck(1L, deckRequest))
                .thenReturn(deckResponse);

        mockMvc.perform(
                        put("/api/decks/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(deckRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.id").value(1))
                .andExpect(jsonPath("$.result.name").value("Japanese N5"))
                .andExpect(jsonPath("$.result.description")
                        .value("Japanese N5 vocabulary"));

        verify(deckService).updateDeck(1L, deckRequest);
    }

    @Test
    void delete_success() throws Exception {
        when(deckService.deleteDeck(1L))
                .thenReturn("Deck has been deleted");

        mockMvc.perform(delete("/api/decks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result").value("Deck has been deleted"));

        verify(deckService).deleteDeck(1L);
    }
}