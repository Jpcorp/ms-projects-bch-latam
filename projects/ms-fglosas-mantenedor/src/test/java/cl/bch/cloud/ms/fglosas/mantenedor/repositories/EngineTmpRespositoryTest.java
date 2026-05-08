package cl.bch.cloud.ms.fglosas.mantenedor.repositories;

import cl.bch.cloud.dto.motor.plantillas.dtos.ConfiguracionesDto;
import cl.bch.cloud.dto.motor.plantillas.dtos.TemplatesRqDTO;
import cl.bch.cloud.dto.motor.plantillas.dtos.TemplatesRs;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.ReqEngineTemplateDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.StatusEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.TemplatesEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.TmpNotificationEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.IlegalActionException;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.NoResponseException;
import cl.bch.cloud.ms.fglosas.mantenedor.restclients.EngineTemplateClient;
import cl.bch.cloud.ms.fglosas.mantenedor.utils.StaticGlossesUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EngineTmpRespositoryTest {

    @Mock
    private EngineTemplateClient client;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private EngineTmpRespository repository;

    @Mock
    private TemplatesEntity templatesEntity;

    @Mock
    private MultipartFile file;

    @BeforeEach
    void setUp() {
        templatesEntity = getTemplatesEntity();
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

    private TemplatesRs getTemplatesRs() {
        String file = "<!DOCTYPE html><html><head><meta charset=\\\"UTF-8\\\"><title>Aviso de Pago</title><style> /* Font Definitions */ @font-face { font-family: Calibri; panose-1: 2 15 5 2 2 2 4 3 2 4; } @font-face { font-family: Tahoma; panose-1: 2 11 6 4 3 5 4 4 2 4; } @font-face { font-family: Consolas; panose-1: 2 11 6 9 2 2 4 3 2 4; } /* Style Definitions */ p.MsoNormal, li.MsoNormal, div.MsoNormal { margin: 0cm; margin-bottom: .0001pt; font-size: 11.0pt; font-family: \\\"Calibri\\\", \\\"sans-serif\\\"; } a:link, span.MsoHyperlink { color: blue; text-decoration: underline; } a:visited, span.MsoHyperlinkFollowed { color: purple; text-decoration: underline; } p.MsoPlainText, li.MsoPlainText, div.MsoPlainText { margin: 0cm; margin-bottom: .0001pt; font-size: 10.5pt; font-family: Consolas; } p.MsoAcetate, li.MsoAcetate, div.MsoAcetate { margin: 0cm; margin-bottom: .0001pt; font-size: 8.0pt; font-family: \\\"Tahoma\\\", \\\"sans-serif\\\"; } span.TextosinformatoCar { font-family: Consolas; } span.TextodegloboCar { font-family: \\\"Tahoma\\\", \\\"sans-serif\\\"; } @page WordSection1 { size: 612.0pt 792.0pt; margin: 70.85pt 3.0cm 70.85pt 3.0cm; } div.WordSection1 { page: WordSection1; } </style></head><body lang=\\\"ES-CL\\\" link=\\\"blue\\\" vlink=\\\"purple\\\"><div class=\\\"WordSection1\\\"><div align=\\\"center\\\"><table class=\\\"MsoNormalTable\\\" border=\\\"0\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\" width=\\\"652\\\" style=\\\"width:489.0pt;background:white\\\"><tbody><tr><td style=\\\"padding:0cm 0cm 0cm 0cm\\\"><div align=\\\"center\\\"><table class=\\\"MsoNormalTable\\\" border=\\\"0\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\" width=\\\"650\\\" style=\\\"width:487.5pt\\\"><tbody><tr style=\\\"height:69.0pt\\\"><td style=\\\"padding:0cm 0cm 0cm 0cm;height:69.0pt\\\"><p class=\\\"MsoNormal\\\"><img width=\\\"650\\\" height=\\\"92\\\" id=\\\"Imagen 70\\\" src=\\\"\\\" /></p></td></tr><tr><td style=\\\"background:white;padding:0cm 0cm 0cm 0cm\\\"><table class=\\\"MsoNormalTable\\\" border=\\\"0\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\" width=\\\"650\\\" style=\\\"width:487.5pt\\\"><tbody><tr><td width=\\\"1\\\" style=\\\"width:.75pt;background:#DBDBDB;padding:0cm 0cm 0cm 0cm\\\"></td><td width=\\\"648\\\" style=\\\"width:486.0pt;padding:0cm 0cm 0cm 0cm\\\"><div align=\\\"center\\\"><table class=\\\"MsoNormalTable\\\" border=\\\"0\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\" width=\\\"595\\\" style=\\\"width:446.25pt\\\"><tbody><tr style=\\\"height:33.0pt\\\"><td width=\\\"595\\\" valign=\\\"bottom\\\" style=\\\"width:446.25pt;padding:0cm 0cm 0cm 0cm;height:33.0pt\\\"><p class=\\\"MsoNormal\\\" align=\\\"right\\\" style=\\\"text-align:right\\\"><span style=\\\"font-size:9.0pt;font-family:'Arial','sans-serif';color:#838689\\\">Santiago, <span>29 de Abril 2025</span></span></p></td></tr><tr style=\\\"height:.75pt\\\"><td style=\\\"background:#CCCCCC;padding:0cm 0cm 0cm 0cm;height:.75pt\\\"></td></tr><tr style=\\\"height:67.5pt\\\"><td style=\\\"padding:0cm 0cm 0cm 0cm;height:67.5pt\\\"><p class=\\\"MsoNormal\\\"><span style=\\\"font-size:15.0pt;font-family:'Arial','sans-serif';color:#0054A6\\\">Servicio de Pagos Bancarios</span></p><p class=\\\"MsoNormal\\\" style='line-height:115%'><span style='font-size:15.0pt;line-height:115%;font-family:\\\"Arial\\\",\\\"sans-serif\\\";color:#0054A6'>Aviso de Pago</span></p></td></tr><tr><td style=\\\"padding:0cm 0cm 0cm 0cm\\\"><p class=\\\"MsoNormal\\\" style=\\\"text-align:justify\\\"><span style=\\\"font-size:11.5pt;font-family:'Arial','sans-serif';color:#7B7B7B\\\"> Estimado Sr. (a): <b>Juanito Perez</b></span></p><p class=\\\"MsoNormal\\\" style='text-align:justify;line-height:115%'><span style='font-size:11.5pt;line-height:115%;font-family:\\\"Arial\\\",\\\"sans-serif\\\";color:#7B7B7B'> e-mail: <span>rcastros@bancochile.cl</span></span></p><p class=\\\"MsoNormal\\\" style=\\\"text-align:justify\\\"><span style=\\\"font-size:11.5pt;font-family:'Arial','sans-serif';color:#7B7B7B\\\"><br/> Informamos a usted que por instrucción de nuestro cliente <b>Jose Luis Cardoza</b>, con fecha <span>29 de Abril de 2025</span>, hemos realizado un abono a su cuenta la cual se encuentra disponible para usted de acuerdo al siguiente detalle: </span></p><p class=\\\"MsoNormal\\\" style=\\\"text-align:justify\\\"><span style=\\\"font-size:11.5pt;font-family:'Arial','sans-serif';color:#7B7B7B\\\"><br/> Medio de Pago : <b></b></span></p><p class=\\\"MsoNormal\\\" style='text-align:justify;line-height:115%'><span style='font-size:11.5pt;line-height:115%;font-family:\\\"Arial\\\",\\\"sans-serif\\\";color:#7B7B7B'> Banco : <b>Banco de Chile</b></span></p><p class=\\\"MsoNormal\\\"><span style=\\\"font-size:11.5pt;font-family:'Arial','sans-serif';color:#7B7B7B\\\"> Monto de Pago : <b>$ 1.000</b>.- </span></p><p class=\\\"MsoNormal\\\" style='text-align:justify;line-height:115%'><span style='font-size:11.5pt;line-height:115%;font-family:\\\"Arial\\\",\\\"sans-serif\\\";color:#7B7B7B'> El concepto del Pago es: <b>Prueba</b></span></p><p class=\\\"MsoNormal\\\" style='text-align:justify;line-height:115%'><span style='font-size:11.5pt;line-height:115%;font-family:\\\"Arial\\\",\\\"sans-serif\\\";color:#7B7B7B'> Detalle: <b><br/></b></span></p><p class=\\\"MsoNormal\\\"><b><span style=\\\"font-family:'Arial','sans-serif';color:#7B7B7B\\\"><span>lorem ipsum</span>: <span>1.000</span><br/></span></b></p><p class=\\\"MsoNormal\\\"><span style=\\\"font-size:11.5pt;font-family:'Arial','sans-serif';color:#7B7B7B\\\"><br/> Atte.<br/> Banco de Chile<br/></span></p></td></tr></tbody></table></div></td><td width=\\\"1\\\" style=\\\"width:.75pt;background:#DBDBDB;padding:0cm 0cm 0cm 0cm\\\"></td></tr></tbody></table></td></tr><tr style=\\\"height:.75pt\\\"><td style=\\\"background:#DBDBDB;padding:0cm 0cm 0cm 0cm;height:.75pt\\\"></td></tr></tbody></table></div></td></tr><tr><td style=\\\"padding:0cm 0cm 0cm 0cm\\\"></td></tr><tr><td style=\\\"padding:0cm 0cm 0cm 0cm\\\"><div align=\\\"center\\\"><table class=\\\"MsoNormalTable\\\" border=\\\"0\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\" width=\\\"650\\\" style=\\\"width:487.5pt\\\"><tbody><tr style=\\\"height:.75pt\\\"><td style=\\\"background:#ECECEC;padding:0cm 0cm 0cm 0cm;height:.75pt\\\"></td></tr><tr style=\\\"height:39.0pt\\\"><td style=\\\"padding:0cm 0cm 0cm 0cm;height:39.0pt\\\"><table class=\\\"MsoNormalTable\\\" border=\\\"0\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\" width=\\\"650\\\" style=\\\"width:487.5pt\\\"><tbody><tr style=\\\"height:56.25pt\\\"><td width=\\\"1\\\" style=\\\"width:.75pt;background:#EEEEEE;padding:0cm 0cm 0cm 0cm;height:56.25pt\\\"></td><td width=\\\"648\\\" style=\\\"width:486.0pt;background:white;padding:0cm 0cm 0cm 0cm;height:56.25pt\\\"><div align=\\\"center\\\"><table class=\\\"MsoNormalTable\\\" border=\\\"0\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\" width=\\\"430\\\" style=\\\"width:322.5pt\\\"><tbody><tr><td width=\\\"33\\\" style=\\\"width:24.75pt;padding:0cm 0cm 0cm 0cm\\\"><p class=\\\"MsoNormal\\\"><img width=\\\"24\\\" height=\\\"24\\\" id=\\\"Imagen 71\\\" alt=\\\"Descripción: Facebook:\\\" src=\\\"\\\" /></p></td><td width=\\\"215\\\" style=\\\"width:161.25pt;padding:0cm 0cm 0cm 0cm\\\"><p class=\\\"MsoNormal\\\"><span style=\\\"font-family:Arial,Helvetica,sans-serif;color:#838689;font-size:12px;font-weight:none\\\">www.bancochile.cl</span></p></td><td width=\\\"33\\\" style=\\\"width:24.75pt;padding:0cm 0cm 0cm 0cm\\\"><p class=\\\"MsoNormal\\\"><img width=\\\"24\\\" height=\\\"24\\\" id=\\\"Imagen 73\\\" alt=\\\"Descripción: Tel:\\\" src=\\\"\\\" /></p></td><td width=\\\"149\\\" style=\\\"width:111.75pt;padding:0cm 0cm 0cm 0cm\\\"><p class=\\\"MsoNormal\\\"><span style=\\\"font-size:9.0pt;font-family:'Arial','sans-serif';color:#838689\\\"> Mesa Ayuda </span><br/><b><span style=\\\"font-size:10.0pt;font-family:'Arial','sans-serif';color:#838689\\\"> 600 637 38 38 </span></b></p></td></tr></tbody></table></div></td><td width=\\\"1\\\" style=\\\"width:.75pt;background:#EEEEEE;padding:0cm 0cm 0cm 0cm;height:56.25pt\\\"></td></tr></tbody></table></td></tr><tr style=\\\"height:.75pt\\\"><td style=\\\"background:#ECECEC;padding:0cm 0cm 0cm 0cm;height:.75pt\\\"></td></tr></tbody></table></div></td></tr></tbody></table><img id=\\\"Imagen 76\\\" alt=\\\"Descripción: Tu clave es clave\\\" src=\\\"\\\" /></div><p class=\\\"MsoNormal\\\"></p></div></body></html>";
        return new TemplatesRs(file, "bch/mdp/email", "/notificacion_v3.html");
    }

    private TemplatesEntity getTemplatesEntity() {
        StatusEntity statusEntity = new StatusEntity();
        statusEntity.setId(1L);
        statusEntity.setName(StaticGlossesUtils.POR_SER_APPROVADO);

        TmpNotificationEntity notificationEntity = new TmpNotificationEntity();
        notificationEntity.setId(2L);

        return new TemplatesEntity(1L, "html", "/notificacion_v3.html", "bch/mdp/email",
                "BCH", "recaudaciones_pagos_servicios", notificationEntity,
                statusEntity, StaticGlossesUtils.PENDTE_X_CARGAR, 0, getattr(),
                "USER_ID", "UPDATE_ID", null, null);
    }


    @Test
    void testGetTemplateByAttr_success() throws Exception {
        ReqEngineTemplateDTO dto = new ReqEngineTemplateDTO("template", "{}", "{}");
        TemplatesRqDTO request = new TemplatesRqDTO();
        ConfiguracionesDto config = new ConfiguracionesDto();
        Map<String, Object> data = Map.of("key", "value");
        TemplatesRs responseDto = getTemplatesRs();

        when(objectMapper.readValue(anyString(), eq(ConfiguracionesDto.class))).thenReturn(config);
        when(objectMapper.readValue(anyString(), eq(Map.class))).thenReturn(data);
        when(client.getTemplate(any())).thenReturn(ResponseEntity.ok(responseDto));

        TemplatesRs result = repository.getTemplateByAttr(templatesEntity);

        assertNotNull(result);
        verify(client).getTemplate(any());
    }

    @Test
    void testGetTemplateByAttr_throwsException() throws Exception {
        when(objectMapper.readValue(anyString(), eq(ConfiguracionesDto.class)))
                .thenThrow(JsonProcessingException.class);

        assertThrows(IlegalActionException.class, () -> repository.getTemplateByAttr(templatesEntity));
    }

    @Test
    void testCreateEnginetmp_success() {
        templatesEntity.setEstadoDocto(StaticGlossesUtils.CARGADO);

        when(client.guardarPlantilla(any(), any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));

        repository.createEnginetmp(templatesEntity, file);

        assertEquals(StaticGlossesUtils.CARGADO, templatesEntity.getEstadoDocto());
    }

    @Test
    void testGetTemplateByAttr_jsonMappingException() throws Exception {
        when(objectMapper.readValue(anyString(), eq(ConfiguracionesDto.class)))
                .thenThrow(new JsonMappingException("mapping error"));
        assertThrows(RuntimeException.class, () -> repository.getTemplateByAttr(templatesEntity));
    }

    @Test
    void testGetTemplateByAttr_genericException() throws Exception {
        when(objectMapper.readValue(anyString(), eq(ConfiguracionesDto.class)))
                .thenThrow(new RuntimeException("generic error"));
        assertThrows(IlegalActionException.class, () -> repository.getTemplateByAttr(templatesEntity));
    }

    @Test
    void testCreateEnginetmp_failure() {
        when(client.guardarPlantilla(any(), any())).thenThrow(new RuntimeException("upload error"));
        assertThrows(NoResponseException.class, () -> repository.createEnginetmp(templatesEntity, file));
    }


}
