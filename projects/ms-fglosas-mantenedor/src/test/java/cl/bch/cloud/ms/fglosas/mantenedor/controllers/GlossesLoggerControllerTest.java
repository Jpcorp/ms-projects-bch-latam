package cl.bch.cloud.ms.fglosas.mantenedor.controllers;

import cl.bch.cloud.ms.fglosas.mantenedor.dtos.LoggerMaintainerDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.LogMantenedorEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.IlegalActionException;
import cl.bch.cloud.ms.fglosas.mantenedor.services.LogMantenedorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GlossesLoggerController.class)
class GlossesLoggerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LogMantenedorService service;

    @Autowired
    private ObjectMapper objectMapper;

    LoggerMaintainerDTO sampleDTO() {
        return new LoggerMaintainerDTO(1L, "GLOSAS_ESTATICAS", 1L,
                "CREATED", "old", "new", "user", LocalDateTime.now());
    }

    @Test
    void testSearchWith_success() throws Exception {
        String from = "2025-07-13";
        String until = "2025-07-14";
        Mockito.when(service.searchWithDate(any(), any()))
                .thenReturn(Optional.of(List.of(sampleDTO())));

        mockMvc.perform(get("/glosses/logs/{from}/{until}", from, until))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].identity").value("GLOSAS_ESTATICAS"));
    }

    @Test
    void testSearchWith_notFound() throws Exception {
        long from = System.currentTimeMillis() - 86400000L;
        long until = System.currentTimeMillis();
        Mockito.when(service.searchWithDate(any(), any()))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/glosses/logs/{from}/{until}", from, until))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetById_found() throws Exception {
        Mockito.when(service.getLoggerById(1L)).thenReturn(Optional.of(sampleDTO()));

        mockMvc.perform(get("/glosses/logs/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identity", is("GLOSAS_ESTATICAS")));

    }

    @Test
    void testGetById_notFound() throws Exception {
        Mockito.when(service.getLoggerById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/glosses/logs/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAll() throws Exception {
        Mockito.when(service.getAllLoggers()).thenReturn(List.of(sampleDTO()));

        mockMvc.perform(get("/glosses/logs/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].identity").value("GLOSAS_ESTATICAS"));
    }

    @Test
    void testCreate_success() throws Exception {
        LogMantenedorEntity entity = sampleDTO().toEntity();
        Mockito.when(service.create(any())).thenReturn(Optional.of(sampleDTO()));

        mockMvc.perform(post("/glosses/logs/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entity)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.identity").value("GLOSAS_ESTATICAS"));
    }

    @Test
    void testCreate_failure() throws Exception {
        LogMantenedorEntity entity = new LogMantenedorEntity();
        Mockito.when(service.create(any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/glosses/logs/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entity)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreate_exception() throws Exception {
        LogMantenedorEntity entity = sampleDTO().toEntity();
        Mockito.when(service.create(any())).thenThrow(
                new IlegalActionException("No OK"));

        mockMvc.perform(post("/glosses/logs/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entity)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.identity").value("GLOSAS_ESTATICAS"));
    }

    @Test
    void testDelete_success() throws Exception {
        Mockito.when(service.delete(1L)).thenReturn(true);

        mockMvc.perform(delete("/glosses/logs/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testDelete_notFound() throws Exception {
        Mockito.when(service.delete(99L)).thenReturn(false);

        mockMvc.perform(delete("/glosses/logs/99"))
                .andExpect(status().isNotFound());
    }
}