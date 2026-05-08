package cl.bch.cloud.ms.fglosas.mantenedor.controllers;

import cl.bch.cloud.ms.fglosas.mantenedor.dtos.AprovalsDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.MarcaDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.RpAllTmpDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.RpIdTmpDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.RqByAttrTmpDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.RqUpdateTmpDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.TmpDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.TransaccionDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.StatusEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.TemplatesEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.TmpNotificationEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.FieldsNotFoundException;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.InvalidTemplateFieldsException;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.TemplateValidationException;
import cl.bch.cloud.ms.fglosas.mantenedor.repositories.MarcaRepository;
import cl.bch.cloud.ms.fglosas.mantenedor.repositories.TransaccionRepository;
import cl.bch.cloud.ms.fglosas.mantenedor.services.TemplatesServices;
import cl.bch.cloud.ms.fglosas.mantenedor.utils.StaticGlossesUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TemplatesController.class)
@ExtendWith(MockitoExtension.class)
class TemplatesControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TemplatesServices service;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private TemplatesController templatesController;

    @MockBean
    private MarcaRepository marcaRepository;

    @MockBean
    private TransaccionRepository transaccionRepository;


    private TemplatesEntity getTemplatesEntity() {
        TmpNotificationEntity notificationEntity = getNotification();

        StatusEntity statusEntity = new StatusEntity();
        statusEntity.setId(3L);
        statusEntity.setName(StaticGlossesUtils.POR_SER_APPROVADO);

        TemplatesEntity entity = new TemplatesEntity();
        entity.setId(1L);
        entity.setAttr(getattr());
        entity.setConfig(notificationEntity);
        entity.setName("/notificacion_v3.html");
        entity.setExtension("html");
        entity.setEstado(statusEntity);
        entity.setUserCreated("USER_ID");
        entity.setUserUpdated("USER_UPDATE_ID");
        entity.setFolder("bch/mdp/email");
        return entity;
    }

    @NotNull
    private static TmpNotificationEntity getNotification() {
        TmpNotificationEntity notificationEntity = new TmpNotificationEntity();
        notificationEntity.setId(1L);
        notificationEntity.setName("test");
        return notificationEntity;
    }

    private String getattr() {
        return "{\n" +
                "\"atributos\": {\n" +
                "        \"tipo\":\"HTML\"\n" +
                "    },\n" +
                "    \"data\" :{\n" +
                "        \"headerImage\": \"\",\n" +
                "        \"fechaCreacion\": \"29 de Abril 2025\",\n" +
                "        \"creditPartyName\": \"Juanito Perez\",\n" +
                "        \"recipientAddress\": \"rcastros@bancochile.cl\",\n" +
                "        \"participantName\": \"Jose Luis Cardoza\",\n" +
                "        \"instructedDay\": \"29 de Abril de 2025\",\n" +
                "        \"bankName\": \"Banco de Chile\",\n" +
                "        \"medioPago\": \"\",\n" +
                "        \"instructedAmount\": 1000,\n" +
                "        \"payrollDescription\": \"Prueba\",\n" +
                "        \"conceptosPago\": [\n" +
                "            {\n" +
                "                \"descripcion\": \"lorem ipsum\",\n" +
                "                \"monto\": 1000\n" +
                "            }\n" +
                "        ],\n" +
                "        \"manoImage\": \"\",\n" +
                "        \"celularImage\": \"\",\n" +
                "        \"footerImage\": \"\"\n" +
                "    }\n" +
                "}";
    }
    @Test
    void testGetAllTemplates() throws Exception {
        RqUpdateTmpDTO request = new RqUpdateTmpDTO(
                3,  1, getattr(), "user_id");
        List<RpAllTmpDTO> response = List.of(RpAllTmpDTO.fromEntity(getTemplatesEntity()));

        when(service.getAll()).thenReturn(response);

        mockMvc.perform(post("/template/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetTemplateById() throws Exception {
        RpIdTmpDTO dto = new RpIdTmpDTO(1, "/notificacion_v3.html", 3, "",
                LocalDate.now(), LocalDate.now());

        RqUpdateTmpDTO request = new RqUpdateTmpDTO(
                3, 1, getattr(),
                "user_id");

        when(service.getByKey(Mockito.anyLong())).thenReturn(Optional.of(dto));

        mockMvc.perform(post("/template/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateTemplate() throws Exception {
        RqUpdateTmpDTO dto = new RqUpdateTmpDTO(
                3, 1, getattr(), "user_id");

        TmpDTO response = new TmpDTO(1, "/notificacion_v3.html", 3, 1,
                getattr(), "user_created", "user_updated",
                "BCH", "pago_proveedores");

        MockMultipartFile file = new MockMultipartFile("file", "test.html", "text/html", "<html></html>".getBytes());
        MockMultipartFile dataPart = new MockMultipartFile(
                "data", "data", "application/json", objectMapper.writeValueAsBytes(dto)
        );
        when(service.updateUpload(Mockito.anyLong(), Mockito.any(), Mockito.any())).thenReturn(Optional.of(response));

        mockMvc.perform(multipart("/template/1")
                        .file(file)
                        .file(dataPart)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk());
    }

    @Test
    void testApproveTemplate() throws Exception {
        AprovalsDTO approval = new AprovalsDTO(1L,
                StaticGlossesUtils.IDENTITY_GLOSSES, 1L, "checker",
                "comentario", LocalDateTime.now(), LocalDateTime.now() );

        TmpDTO response = new TmpDTO(1, "/notificacion_v3.html", 3, 1,
                getattr(), "user_created", "user_updated", "BCH", "pago_proveedores");

        when(service.approve(Mockito.anyLong(), Mockito.any())).thenReturn(Optional.of(response));

        mockMvc.perform(post("/template/1/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(approval)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetByIdAndAttr() throws Exception {
        RqByAttrTmpDTO rq = new RqByAttrTmpDTO("BCH",
                "recaudaciones_pagos_servicios", null );
        RpIdTmpDTO dto = new RpIdTmpDTO(1, "/notificacion_v3.html", 3, "", LocalDate.now(), LocalDate.now());
        when(service.getByAttr(any())).thenReturn(Optional.of(dto));

        mockMvc.perform(post("/template/get")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rq)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateTemplateThrowsException() throws Exception {
        RqUpdateTmpDTO dto = new RqUpdateTmpDTO(3, 1, getattr(), "user_id");
        MockMultipartFile file = new MockMultipartFile("file", "test.html", "text/html", "<html></html>".getBytes());
        MockMultipartFile dataPart = new MockMultipartFile(
                "data", "data", "application/json", objectMapper.writeValueAsBytes(dto)
        );

        when(service.updateUpload(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenThrow(new IllegalStateException("Error de prueba"));

        assertThrows(ServletException.class, () -> {
            mockMvc.perform(multipart("/template/1")
                    .file(file)
                    .file(dataPart)
                    .with(request -> {
                        request.setMethod("PUT");
                        return request;
                    }));
        });
    }


        @Test
        void testCreateUpload_InvalidTemplateFieldsException() throws Exception {
            TmpDTO dto = new TmpDTO(1, "/notificacion_v3.html", 3, 1, "{}",
                    "user_created", "user_updated", "BCH", "pago_proveedores");
            MockMultipartFile file = new MockMultipartFile("file", "test.html", "text/html", "<html></html>".getBytes());
            MockMultipartFile dataPart = new MockMultipartFile("data", "data", "application/json", objectMapper.writeValueAsBytes(dto));

            List<String> invalidFields = new ArrayList<>();
            invalidFields.add("Campo_x");

            when(service.createUpload(Mockito.any(), Mockito.any(), anyString(), anyString()))

                    .thenThrow(new InvalidTemplateFieldsException(invalidFields));

            mockMvc.perform(multipart("/template/")
                            .file(file)
                            .file(dataPart))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void testCreateUpload_IOException() throws Exception {
            TmpDTO dto = new TmpDTO(1, "/notificacion_v3.html", 3, 1, "{}",
                    "user_created", "user_updated", "BCH", "pago_proveedores");
            MockMultipartFile file = new MockMultipartFile("file", "test.html", "text/html", "<html></html>".getBytes());
            MockMultipartFile dataPart = new MockMultipartFile("data", "data", "application/json", objectMapper.writeValueAsBytes(dto));

            when(service.createUpload(Mockito.any(), Mockito.any(), anyString(), anyString()))
                    .thenThrow(new IOException("Error al leer el archivo"));

            mockMvc.perform(multipart("/template/")
                            .file(file)
                            .file(dataPart))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        void testApproveTemplate_Exception() throws Exception {
            AprovalsDTO approval = new AprovalsDTO(1L, "IDENTITY_GLOSSES", 1L, "checker", "comentario", LocalDateTime.now(), LocalDateTime.now());

            when(service.approve(Mockito.anyLong(), Mockito.any())).thenThrow(new RuntimeException("Error"));

            mockMvc.perform(post("/template/1/approve")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(approval)))
                    .andExpect(status().isBadRequest());
        }


    @Test
    void obtenerMarcas_deberiaRetornarListaDeMarcaDTO() throws Exception {

        List<MarcaDTO> mockMarcas = List.of(
                new MarcaDTO("M001", "Marca Uno"),
                new MarcaDTO("M002", "Marca Dos")
        );

        Mockito.when(service.getAllMarcas()).thenReturn(mockMarcas);

        mockMvc.perform(MockMvcRequestBuilders.post("/template/marcas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value("M001"))
                .andExpect(jsonPath("$[0].nombre").value("Marca Uno"))
                .andExpect(jsonPath("$[1].id").value("M002"))
                .andExpect(jsonPath("$[1].nombre").value("Marca Dos"));
    }


    @Test
    void obtenerTransaccionesPorMarca_deberiaRetornarListaDeTransaccionDTO() throws Exception {

        String idMarca = "M123";
        List<TransaccionDTO> mockTransacciones = List.of(
                new TransaccionDTO(1L, "Compra supermercado", idMarca, "Transacción 1"),
                new TransaccionDTO(2L, "Pago servicio", idMarca, "Transacción 2")
        );

        Mockito.when(service.obtenerTransaccionesPorMarca(idMarca)).thenReturn(mockTransacciones);


        mockMvc.perform(MockMvcRequestBuilders.post("/template/transacciones")
                        .content(idMarca)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].descripcion").value("Compra supermercado"))
                .andExpect(jsonPath("$[0].marcaId").value(idMarca))
                .andExpect(jsonPath("$[0].nombre").value("Transacción 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].descripcion").value("Pago servicio"))
                .andExpect(jsonPath("$[1].marcaId").value(idMarca))
                .andExpect(jsonPath("$[1].nombre").value("Transacción 2"));
    }


    @Test
    void testCreateUpload_FieldsNotFoundException() throws Exception {
        TmpDTO dto = new TmpDTO(1, "/notificacion_v3.html", 3, 1, "{}",
                "user_created", "user_updated", "BCH", "pago_proveedores");
        MockMultipartFile file = new MockMultipartFile("file", "test.html", "text/html", "<html></html>".getBytes());
        MockMultipartFile dataPart = new MockMultipartFile("data", "data", "application/json", objectMapper.writeValueAsBytes(dto));

        List<String> fieldsNotFound = new ArrayList<>();
        fieldsNotFound.add("txt_1");
        when(service.createUpload(Mockito.any(), Mockito.any(), anyString(), anyString()))
                .thenThrow(new FieldsNotFoundException(fieldsNotFound));

        mockMvc.perform(multipart("/template/")
                        .file(file)
                        .file(dataPart))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateUpload_TemplateValidationException() throws Exception {
        TmpDTO dto = new TmpDTO(1, "/notificacion_v3.html", 3, 1, "{}",
                "user_created", "user_updated", "BCH", "pago_proveedores");
        MockMultipartFile file = new MockMultipartFile("file", "test.html", "text/html", "<html></html>".getBytes());
        MockMultipartFile dataPart = new MockMultipartFile("data", "data", "application/json", objectMapper.writeValueAsBytes(dto));

        Throwable cause = new Throwable();
        cause.getMessage();

        when(service.createUpload(Mockito.any(), Mockito.any(), anyString(), anyString()))
                .thenThrow(new TemplateValidationException("Error de validación general", cause));

        mockMvc.perform(multipart("/template/")
                        .file(file)
                        .file(dataPart))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetTemplateById_NotFound() throws Exception {
        when(service.getByKey(Mockito.anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(post("/template/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}