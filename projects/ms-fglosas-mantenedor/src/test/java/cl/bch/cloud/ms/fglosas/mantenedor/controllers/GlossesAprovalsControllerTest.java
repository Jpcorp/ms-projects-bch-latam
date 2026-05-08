package cl.bch.cloud.ms.fglosas.mantenedor.controllers;

import cl.bch.cloud.ms.fglosas.mantenedor.dtos.AprovalsDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.AprobacionesEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.IlegalActionException;
import cl.bch.cloud.ms.fglosas.mantenedor.services.AprovalsService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GlossesAprovalsController.class)
class GlossesAprovalsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AprovalsService aprovalsService;

    @Autowired
    private ObjectMapper objectMapper;

    private AprovalsDTO sampleDTO() {
        return new AprovalsDTO(
                1L,
                "GLOSAS_ESTATICAS",
                1L,
                "checker",
                "comentario",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void testGetById_found() throws Exception {
        Mockito.when(aprovalsService.getApprovalsById(1L)).thenReturn(Optional.of(sampleDTO()));

        mockMvc.perform(get("/glosses/aprovals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identity").value("GLOSAS_ESTATICAS"));
    }

    @Test
    void testGetById_notFound() throws Exception {
        Mockito.when(aprovalsService.getApprovalsById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/glosses/aprovals/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAll() throws Exception {
        Mockito.when(aprovalsService.getAllAprovals()).thenReturn(List.of(sampleDTO()));

        mockMvc.perform(get("/glosses/aprovals/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].identity").value("GLOSAS_ESTATICAS"));
    }

    @Test
    void testCreate_success() throws Exception {
        AprobacionesEntity entity = new AprobacionesEntity();
        Mockito.when(aprovalsService.create(any())).thenReturn(Optional.of(sampleDTO()));

        mockMvc.perform(post("/glosses/aprovals/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entity)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.identity").value("GLOSAS_ESTATICAS"));
    }

    @Test
    void testCreate_failure() throws Exception {
        AprobacionesEntity entity = new AprobacionesEntity();
        Mockito.when(aprovalsService.create(any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/glosses/aprovals/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entity)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreate_exception() throws Exception {
        AprobacionesEntity entity = new AprobacionesEntity();
        Mockito.when(aprovalsService.create(any())).thenThrow(
                new IlegalActionException("NOT OK"));

        mockMvc.perform(post("/glosses/aprovals/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entity)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdate_success() throws Exception {
        AprobacionesEntity entity = new AprobacionesEntity();
        Mockito.when(aprovalsService.update(eq(1L), any())).thenReturn(Optional.of(sampleDTO()));

        mockMvc.perform(put("/glosses/aprovals/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entity)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identity").value("GLOSAS_ESTATICAS"));
    }

    @Test
    void testUpdate_notFound() throws Exception {
        AprobacionesEntity entity = new AprobacionesEntity();
        Mockito.when(aprovalsService.update(eq(1L), any())).thenReturn(Optional.empty());

        mockMvc.perform(put("/glosses/aprovals/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entity)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDelete_success() throws Exception {
        Mockito.when(aprovalsService.delete(1L)).thenReturn(true);

        mockMvc.perform(delete("/glosses/aprovals/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testDelete_notFound() throws Exception {
        Mockito.when(aprovalsService.delete(99L)).thenReturn(false);

        mockMvc.perform(delete("/glosses/aprovals/99"))
                .andExpect(status().isNotFound());
    }
}
