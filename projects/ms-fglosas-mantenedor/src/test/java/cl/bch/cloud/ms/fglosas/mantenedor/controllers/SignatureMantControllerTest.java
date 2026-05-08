package cl.bch.cloud.ms.fglosas.mantenedor.controllers;

import cl.bch.cloud.ms.fglosas.mantenedor.dtos.AprovalsDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.SignaturesDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.StatusEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.services.SignaturesService;
import cl.bch.cloud.ms.fglosas.mantenedor.utils.StaticGlossesUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SignatureMantController.class)
public class SignatureMantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SignaturesService service;

    @Autowired
    private ObjectMapper objectMapper;

    private StatusEntity getStatusEntity() {
        StatusEntity statusEntity = new StatusEntity();
        statusEntity.setId(1L);
        statusEntity.setName(StaticGlossesUtils.POR_SER_APPROVADO);
        statusEntity.setCreateAt(LocalDateTime.now());
        return statusEntity;
    }

    private SignaturesDTO sampleSignature() {

        SignaturesDTO dto = new SignaturesDTO(
                1L, "16742032-k", "Test Signature", "admin",
                "KJJDKKFDJ", 1L, LocalDateTime.now());
        return dto;
    }

    private AprovalsDTO sampleApproval() {
        AprovalsDTO dto = new AprovalsDTO(
                1L, StaticGlossesUtils.IDENTITY_SIGNATURE, 1L, "VCACERES",
                "OK", LocalDateTime.now(), LocalDateTime.now());
        return dto;
    }

    @Test
    void testGetById_found() throws Exception {
        Mockito.when(service.getById(1L)).thenReturn(Optional.of(sampleSignature()));
        mockMvc.perform(get("/signature/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetById_notFound() throws Exception {
        Mockito.when(service.getById(1L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/signature/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAll_nonEmpty() throws Exception {
        Mockito.when(service.getAll()).thenReturn(Collections.singletonList(sampleSignature()));
        mockMvc.perform(get("/signature/"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAll_empty() throws Exception {
        Mockito.when(service.getAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/signature/"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreate_success() throws Exception {
        Mockito.when(service.create(any())).thenReturn(Optional.of(sampleSignature()));
        mockMvc.perform(post("/signature/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleSignature())))
                .andExpect(status().isCreated());
    }

    @Test
    void testDelete_success() throws Exception {
        Mockito.when(service.delete(eq(1L), any())).thenReturn(true);
        mockMvc.perform(delete("/signature/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleApproval())))
                .andExpect(status().isOk());
    }

    @Test
    void testDelete_notFound() throws Exception {
        Mockito.when(service.delete(eq(1L), any())).thenReturn(false);
        mockMvc.perform(delete("/signature/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleApproval())))
                .andExpect(status().isNotFound());
    }

    @Test
    void testApprove_success() throws Exception {
        Mockito.when(service.approve(eq(1L), any())).thenReturn(Optional.of(sampleSignature()));
        mockMvc.perform(post("/signature/1/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleApproval())))
                .andExpect(status().isOk());
    }

    @Test
    void testApprove_notFound() throws Exception {
        Mockito.when(service.approve(eq(1L), any())).thenReturn(Optional.empty());
        mockMvc.perform(post("/signature/1/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleApproval())))
                .andExpect(status().isNotFound());
    }

    @Test
    void testApprove_exception() throws Exception {
        Mockito.when(service.approve(eq(1L), any())).thenThrow(new RuntimeException("Simulated error"));
        mockMvc.perform(post("/signature/1/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleApproval())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEnableOrDisable_success() throws Exception {
        Long[] ids = {1L, 3L};
        Mockito.when(service.toggleStatus(eq(ids), any())).thenReturn(true);
        mockMvc.perform(post("/signature/change/1/3/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleApproval())))
                .andExpect(status().isOk());
    }

    @Test
    void testEnableOrDisable_failure() throws Exception {
        Long[] ids = {1L, 3L};
        Mockito.when(service.toggleStatus(eq(ids), any())).thenReturn(false);
        mockMvc.perform(post("/signature/change/1/3/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleApproval())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEnableOrDisable_partialFailure() throws Exception {
        Long[] ids = {1L, 2L};
        Mockito.when(service.toggleStatus(eq(ids), any())).thenReturn(false);

        mockMvc.perform(post("/signature/change/1/2/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleApproval())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEnableOrDisable_exceptionHandling() throws Exception {
        Long[] ids = {1L, 2L};
        Mockito.when(service.toggleStatus(eq(ids), any()))
                .thenThrow(new RuntimeException("Simulated error"));

        mockMvc.perform(post("/signature/change/1/2/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleApproval())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdate_success() throws Exception {
        Mockito.when(service.update(anyLong(), anyString())).thenReturn(Optional.of(sampleSignature()));
        mockMvc.perform(put("/signature/1/tester"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdate_illegalStateException() throws Exception {
        Mockito.when(service.update(anyLong(), anyString())).thenThrow(new IllegalStateException("Error simulado"));
        mockMvc.perform(put("/signature/1/tester"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEnable_success() throws Exception {
        Mockito.when(service.enable(eq(1L), any())).thenReturn(true);

        mockMvc.perform(post("/signature/change/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleApproval())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("200"))
                .andExpect(jsonPath("$.message").value("Estado de glosa actualizado exitosamente"));
    }

}