package cl.bch.cloud.ms.fglosas.mantenedor.controllers;

import cl.bch.cloud.ms.fglosas.mantenedor.dtos.AprovalsDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.StaticGlossesDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.IlegalActionException;
import cl.bch.cloud.ms.fglosas.mantenedor.services.StaticGlossesService;
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
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(GlossesMantController.class)
class GlossesMantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StaticGlossesService staticGlossesService;

    @Autowired
    private ObjectMapper objectMapper;

    StaticGlossesDTO sampleDTO() {
        return new StaticGlossesDTO(1L, "name", "value", "user",
                "user", 1, 1, LocalDateTime.now(), LocalDateTime.now());
    }

    AprovalsDTO sampleAprovalsDTO() {
        return new AprovalsDTO(2L, StaticGlossesUtils.IDENTITY_GLOSSES,
                1L, "SYSTEM", "OK", LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void testGetById_found() throws Exception {
        Mockito.when(staticGlossesService.getGlossesById(1L)).thenReturn(Optional.of(sampleDTO()));

        mockMvc.perform(post("/glosses/static/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testGetById_notFound() throws Exception {
        Mockito.when(staticGlossesService.getGlossesById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/glosses/static/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAll() throws Exception {
        Mockito.when(staticGlossesService.getAllGlosses()).thenReturn(List.of(sampleDTO()));

        mockMvc.perform(post("/glosses/static/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("name"));
    }

    @Test
    void testCreate_success() throws Exception {
        StaticGlossesDTO dto = sampleDTO();
        Mockito.when(staticGlossesService.create(any())).thenReturn(Optional.of(dto));

        mockMvc.perform(post("/glosses/static/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("name"));
    }

    @Test
    void testCreate_failure() throws Exception {
        StaticGlossesDTO dto = sampleDTO();
        Mockito.when(staticGlossesService.create(any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/glosses/static/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testUpdate_success() throws Exception {
        StaticGlossesDTO dto = sampleDTO();
        Mockito.when(staticGlossesService.update(eq(1L), any())).thenReturn(Optional.of(dto));

        mockMvc.perform(put("/glosses/static/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("name"));
    }

    @Test
    void testUpdate_exception() throws Exception {
        StaticGlossesDTO dto = sampleDTO();
        // Simula una excepción al llamar al servicio de actualización
        Mockito.when(staticGlossesService.update(eq(1L), any()))
                .thenThrow(new IlegalActionException("Simulated error"));

        // Ejecuta la solicitud PUT y verifica que se maneje la excepción
        mockMvc.perform(put("/glosses/static/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdate_notFound() throws Exception {
        StaticGlossesDTO dto = sampleDTO();
        Mockito.when(staticGlossesService.update(eq(1L), any())).thenReturn(Optional.empty());

        mockMvc.perform(put("/glosses/static/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDelete_success() throws Exception {
        AprovalsDTO approval = sampleAprovalsDTO();
        Mockito.when(staticGlossesService.delete(eq(1L), any())).thenReturn(true);

        mockMvc.perform(post("/glosses/static/delete/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(approval)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("true"))
                .andExpect(jsonPath("$.message").value("Objeto eliminado exitosamente"));
    }

    @Test
    void testDelete_notFound() throws Exception {
        AprovalsDTO approval = sampleAprovalsDTO();
        Mockito.when(staticGlossesService.delete(eq(1L), any())).thenReturn(false);

        mockMvc.perform(post("/glosses/static/delete/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(approval)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("false"))
                .andExpect(jsonPath("$.message").value("Objeto no eliminado"));
    }

    @Test
    void testApprove_success() throws Exception {
        StaticGlossesDTO dto = sampleDTO();
        AprovalsDTO approval = sampleAprovalsDTO();

        Mockito.when(staticGlossesService.aproveAndActivate(eq(1L), any()))
                .thenReturn(Optional.of(dto));

        mockMvc.perform(post("/glosses/static/1/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(approval)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("name"));
    }

    @Test
    void testApprove_failure() throws Exception {
        AprovalsDTO approval = new AprovalsDTO(1L, "GLOSAS_ESTATICAS", 1L, "checker", "comentario", LocalDateTime.now(), LocalDateTime.now());

        Mockito.when(staticGlossesService.glossesAproved(eq(1L), any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/glosses/static/1/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(approval)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEnableOrDisable_success() throws Exception {
        AprovalsDTO approval = sampleAprovalsDTO();
        Mockito.when(staticGlossesService.toggleStatus(eq(1L), any())).thenReturn(true);

        mockMvc.perform(post("/glosses/static/change/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(approval)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("200"))
                .andExpect(jsonPath("$.message").value("Estado de glosa actualizado exitosamente"));
    }

    @Test
    void testEnableOrDisable_failure() throws Exception {
        AprovalsDTO approval = sampleAprovalsDTO();
        Mockito.when(staticGlossesService.toggleStatus(eq(1L), any())).thenReturn(false);

        mockMvc.perform(post("/glosses/static/change/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(approval)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.message").value("No se ha cambiado el estado de la glosa"));
    }

    @Test
    void testEnableOrDisable_exception() throws Exception {
        AprovalsDTO approval = sampleAprovalsDTO();
        Mockito.when(staticGlossesService.toggleStatus(eq(1L), any()))
                .thenThrow(new IllegalStateException("Error simulado"));

        mockMvc.perform(post("/glosses/static/change/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(approval)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testApprove_notFound() throws Exception {
        AprovalsDTO approval = sampleAprovalsDTO();

        Mockito.when(staticGlossesService.aproveAndActivate(eq(1L), any()))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/glosses/static/1/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(approval)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testApprove_exception() throws Exception {
        AprovalsDTO approval = sampleAprovalsDTO();

        Mockito.when(staticGlossesService.aproveAndActivate(eq(1L), any()))
                .thenThrow(new RuntimeException("Simulated error"));

        mockMvc.perform(post("/glosses/static/1/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(approval)))
                .andExpect(status().isConflict());
    }

    @Test
    void testByName_succes() throws Exception {
        StaticGlossesDTO dto = sampleDTO();

        Mockito.when(staticGlossesService.getGlossesByName(anyString()))
                        .thenReturn(Optional.of(dto));

        mockMvc.perform(post("/glosses/static/name")
                        .param("name", "pagoComercio")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testByName_notFound() throws Exception {
        Mockito.when(staticGlossesService.getGlossesByName(anyString()))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/glosses/static/name")
                        .param("name", "inexistente")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}