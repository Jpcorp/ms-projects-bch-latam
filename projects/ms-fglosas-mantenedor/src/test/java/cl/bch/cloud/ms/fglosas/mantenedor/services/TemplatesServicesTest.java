package cl.bch.cloud.ms.fglosas.mantenedor.services;
import cl.bch.cloud.dto.motor.plantillas.dtos.TemplatesRs;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.*;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.*;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.IlegalActionException;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.NoDataFoundException;
import cl.bch.cloud.ms.fglosas.mantenedor.repositories.*;
import cl.bch.cloud.ms.fglosas.mantenedor.utils.StaticGlossesUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.exceptions.misusing.PotentialStubbingProblem;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TemplatesServicesTest {

    private  TemplatesRepository repository;
    private  StatusService statusService;
    private  AprovalsService aprovalsService;
    private  LogMantenedorService loggerService;
    private  EngineTmpRespository engineTmpRespository;
    private  TmpNotificationRepository notifyRespository;
    private  SignaturesService signaturesService;
    private TemplatesServices service;

    private CampoRepository campoRepository;
    private MarcaRepository marcaRepository;
    private TransaccionRepository transaccionRepository;

    String mark = "BCH";
    String transaccion = "8";

    @BeforeEach
    void setUp() {
        repository = mock(TemplatesRepository.class);
        statusService = mock(StatusService.class);
        aprovalsService = mock(AprovalsService.class);
        loggerService = mock(LogMantenedorService.class);
        engineTmpRespository = mock(EngineTmpRespository.class);
        notifyRespository = mock(TmpNotificationRepository.class);
        signaturesService = mock(SignaturesService.class);
        campoRepository = mock(CampoRepository.class);
        marcaRepository = mock(MarcaRepository.class);
        transaccionRepository = mock(TransaccionRepository.class);
        service = new TemplatesServices(
                repository,
                statusService,
                aprovalsService,
                loggerService,
                engineTmpRespository,
                notifyRespository,
                signaturesService,
                campoRepository,
                marcaRepository,
                transaccionRepository);

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

    private SignaturesEntity sampleSignature() {

        SignaturesEntity dto = new SignaturesEntity(
                1L, "16742032-k", "Test Signature", "admin",
                "KJJDKKFDJ", getStatusEntity(), LocalDateTime.now());
        return dto;
    }

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
        entity.setMarca("BCH");
        entity.setTransaccion("pago_proveedores");
        return entity;
    }

    @NotNull
    private static TmpNotificationEntity getNotification() {
        TmpNotificationEntity notificationEntity = new TmpNotificationEntity();
        notificationEntity.setId(1L);
        notificationEntity.setName("test");
        return notificationEntity;
    }

    @NotNull
    private static TransaccionEntity getTransacction() {
        TransaccionEntity transaccionEntity = new TransaccionEntity();
        transaccionEntity.setId(1L);
        transaccionEntity.setDescripcion("recaudaciones_pagos_servicios");
        transaccionEntity.setNombre("Recaudaciones y Pagos de Servicios");

        MarcaEntity marca = new MarcaEntity();
        marca.setId("BCH");
        marca.setNombre("Banco de Chile");
        transaccionEntity.setMarca(marca);

        return transaccionEntity;
    }

    @NotNull
    public static StatusEntity getStatusEntity() {
        return new StatusEntity(
                1L, StaticGlossesUtils.ACTIVO, LocalDateTime.of(2023, 1, 2, 10, 0));
    }

    @NotNull
    public static StatusEntity getStatusInactiveEntity() {
        return new StatusEntity(
                2L, StaticGlossesUtils.INACTIVO, LocalDateTime.of(2023, 1, 2, 10, 0));
    }

    @NotNull
    public static StatusEntity getStatusByApproveEntity() {
        return new StatusEntity(
                1L, StaticGlossesUtils.POR_SER_APPROVADO, LocalDateTime.of(2023, 1, 2, 10, 0));
    }

    @NotNull
    private static AprobacionesEntity getAprovals(TemplatesEntity entity) {
        AprobacionesEntity aprovals = new AprobacionesEntity();
        aprovals.setId(1L);
        aprovals.setIdentity(StaticGlossesUtils.ACTIVO);
        aprovals.setIdentityId(entity.getId());
        aprovals.setComentario("Comentario");
        aprovals.setChecker("Checker");
        aprovals.setCreateAt(LocalDateTime.now());
        return aprovals;
    }
    @NotNull
    private static DominioDTO getDominioDTO() {
        NotificacionDTO notificacion = new NotificacionDTO(1, "email", "/email");
        CanalDTO canalDTO = new CanalDTO("mdp", List.of(notificacion), 1L);
        DominioDTO dominioDTO = new DominioDTO("bch", List.of(canalDTO));
        return dominioDTO;
    }

    private TmpNotificationEntity getTmpNotificationEntityList() {
        TmpDomainsEntity tmpDomainsEntity = new TmpDomainsEntity();
        tmpDomainsEntity.setId(1L);
        tmpDomainsEntity.setName("BCH");
        tmpDomainsEntity.setFolder("/bch");
        tmpDomainsEntity.setCreateAt(LocalDate.now());

        TmpChannelEntity channelEntity = new TmpChannelEntity();
        channelEntity.setId(1L);
        channelEntity.setName("MDP");
        channelEntity.setFolder("/mdp");
        channelEntity.setCreateAt(LocalDate.now());
        channelEntity.setDominio(tmpDomainsEntity);

        TmpNotificationEntity notificationEntity = new TmpNotificationEntity();
        notificationEntity.setId(1L);
        notificationEntity.setName("test");
        notificationEntity.setCanal(channelEntity);
        return notificationEntity;
    }

    private TemplatesRs getTemplatesRs() {
        String file = "<!DOCTYPE html><html><head><meta charset=\\\"UTF-8\\\"><title>Aviso de Pago</title><style> /* Font Definitions */ @font-face { font-family: Calibri; panose-1: 2 15 5 2 2 2 4 3 2 4; } @font-face { font-family: Tahoma; panose-1: 2 11 6 4 3 5 4 4 2 4; } @font-face { font-family: Consolas; panose-1: 2 11 6 9 2 2 4 3 2 4; } /* Style Definitions */ p.MsoNormal, li.MsoNormal, div.MsoNormal { margin: 0cm; margin-bottom: .0001pt; font-size: 11.0pt; font-family: \\\"Calibri\\\", \\\"sans-serif\\\"; } a:link, span.MsoHyperlink { color: blue; text-decoration: underline; } a:visited, span.MsoHyperlinkFollowed { color: purple; text-decoration: underline; } p.MsoPlainText, li.MsoPlainText, div.MsoPlainText { margin: 0cm; margin-bottom: .0001pt; font-size: 10.5pt; font-family: Consolas; } p.MsoAcetate, li.MsoAcetate, div.MsoAcetate { margin: 0cm; margin-bottom: .0001pt; font-size: 8.0pt; font-family: \\\"Tahoma\\\", \\\"sans-serif\\\"; } span.TextosinformatoCar { font-family: Consolas; } span.TextodegloboCar { font-family: \\\"Tahoma\\\", \\\"sans-serif\\\"; } @page WordSection1 { size: 612.0pt 792.0pt; margin: 70.85pt 3.0cm 70.85pt 3.0cm; } div.WordSection1 { page: WordSection1; } </style></head><body lang=\\\"ES-CL\\\" link=\\\"blue\\\" vlink=\\\"purple\\\"><div class=\\\"WordSection1\\\"><div align=\\\"center\\\"><table class=\\\"MsoNormalTable\\\" border=\\\"0\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\" width=\\\"652\\\" style=\\\"width:489.0pt;background:white\\\"><tbody><tr><td style=\\\"padding:0cm 0cm 0cm 0cm\\\"><div align=\\\"center\\\"><table class=\\\"MsoNormalTable\\\" border=\\\"0\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\" width=\\\"650\\\" style=\\\"width:487.5pt\\\"><tbody><tr style=\\\"height:69.0pt\\\"><td style=\\\"padding:0cm 0cm 0cm 0cm;height:69.0pt\\\"><p class=\\\"MsoNormal\\\"><img width=\\\"650\\\" height=\\\"92\\\" id=\\\"Imagen 70\\\" src=\\\"\\\" /></p></td></tr><tr><td style=\\\"background:white;padding:0cm 0cm 0cm 0cm\\\"><table class=\\\"MsoNormalTable\\\" border=\\\"0\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\" width=\\\"650\\\" style=\\\"width:487.5pt\\\"><tbody><tr><td width=\\\"1\\\" style=\\\"width:.75pt;background:#DBDBDB;padding:0cm 0cm 0cm 0cm\\\"></td><td width=\\\"648\\\" style=\\\"width:486.0pt;padding:0cm 0cm 0cm 0cm\\\"><div align=\\\"center\\\"><table class=\\\"MsoNormalTable\\\" border=\\\"0\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\" width=\\\"595\\\" style=\\\"width:446.25pt\\\"><tbody><tr style=\\\"height:33.0pt\\\"><td width=\\\"595\\\" valign=\\\"bottom\\\" style=\\\"width:446.25pt;padding:0cm 0cm 0cm 0cm;height:33.0pt\\\"><p class=\\\"MsoNormal\\\" align=\\\"right\\\" style=\\\"text-align:right\\\"><span style=\\\"font-size:9.0pt;font-family:'Arial','sans-serif';color:#838689\\\">Santiago, <span>29 de Abril 2025</span></span></p></td></tr><tr style=\\\"height:.75pt\\\"><td style=\\\"background:#CCCCCC;padding:0cm 0cm 0cm 0cm;height:.75pt\\\"></td></tr><tr style=\\\"height:67.5pt\\\"><td style=\\\"padding:0cm 0cm 0cm 0cm;height:67.5pt\\\"><p class=\\\"MsoNormal\\\"><span style=\\\"font-size:15.0pt;font-family:'Arial','sans-serif';color:#0054A6\\\">Servicio de Pagos Bancarios</span></p><p class=\\\"MsoNormal\\\" style='line-height:115%'><span style='font-size:15.0pt;line-height:115%;font-family:\\\"Arial\\\",\\\"sans-serif\\\";color:#0054A6'>Aviso de Pago</span></p></td></tr><tr><td style=\\\"padding:0cm 0cm 0cm 0cm\\\"><p class=\\\"MsoNormal\\\" style=\\\"text-align:justify\\\"><span style=\\\"font-size:11.5pt;font-family:'Arial','sans-serif';color:#7B7B7B\\\"> Estimado Sr. (a): <b>Juanito Perez</b></span></p><p class=\\\"MsoNormal\\\" style='text-align:justify;line-height:115%'><span style='font-size:11.5pt;line-height:115%;font-family:\\\"Arial\\\",\\\"sans-serif\\\";color:#7B7B7B'> e-mail: <span>rcastros@bancochile.cl</span></span></p><p class=\\\"MsoNormal\\\" style=\\\"text-align:justify\\\"><span style=\\\"font-size:11.5pt;font-family:'Arial','sans-serif';color:#7B7B7B\\\"><br/> Informamos a usted que por instrucción de nuestro cliente <b>Jose Luis Cardoza</b>, con fecha <span>29 de Abril de 2025</span>, hemos realizado un abono a su cuenta la cual se encuentra disponible para usted de acuerdo al siguiente detalle: </span></p><p class=\\\"MsoNormal\\\" style=\\\"text-align:justify\\\"><span style=\\\"font-size:11.5pt;font-family:'Arial','sans-serif';color:#7B7B7B\\\"><br/> Medio de Pago : <b></b></span></p><p class=\\\"MsoNormal\\\" style='text-align:justify;line-height:115%'><span style='font-size:11.5pt;line-height:115%;font-family:\\\"Arial\\\",\\\"sans-serif\\\";color:#7B7B7B'> Banco : <b>Banco de Chile</b></span></p><p class=\\\"MsoNormal\\\"><span style=\\\"font-size:11.5pt;font-family:'Arial','sans-serif';color:#7B7B7B\\\"> Monto de Pago : <b>$ 1.000</b>.- </span></p><p class=\\\"MsoNormal\\\" style='text-align:justify;line-height:115%'><span style='font-size:11.5pt;line-height:115%;font-family:\\\"Arial\\\",\\\"sans-serif\\\";color:#7B7B7B'> El concepto del Pago es: <b>Prueba</b></span></p><p class=\\\"MsoNormal\\\" style='text-align:justify;line-height:115%'><span style='font-size:11.5pt;line-height:115%;font-family:\\\"Arial\\\",\\\"sans-serif\\\";color:#7B7B7B'> Detalle: <b><br/></b></span></p><p class=\\\"MsoNormal\\\"><b><span style=\\\"font-family:'Arial','sans-serif';color:#7B7B7B\\\"><span>lorem ipsum</span>: <span>1.000</span><br/></span></b></p><p class=\\\"MsoNormal\\\"><span style=\\\"font-size:11.5pt;font-family:'Arial','sans-serif';color:#7B7B7B\\\"><br/> Atte.<br/> Banco de Chile<br/></span></p></td></tr></tbody></table></div></td><td width=\\\"1\\\" style=\\\"width:.75pt;background:#DBDBDB;padding:0cm 0cm 0cm 0cm\\\"></td></tr></tbody></table></td></tr><tr style=\\\"height:.75pt\\\"><td style=\\\"background:#DBDBDB;padding:0cm 0cm 0cm 0cm;height:.75pt\\\"></td></tr></tbody></table></div></td></tr><tr><td style=\\\"padding:0cm 0cm 0cm 0cm\\\"></td></tr><tr><td style=\\\"padding:0cm 0cm 0cm 0cm\\\"><div align=\\\"center\\\"><table class=\\\"MsoNormalTable\\\" border=\\\"0\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\" width=\\\"650\\\" style=\\\"width:487.5pt\\\"><tbody><tr style=\\\"height:.75pt\\\"><td style=\\\"background:#ECECEC;padding:0cm 0cm 0cm 0cm;height:.75pt\\\"></td></tr><tr style=\\\"height:39.0pt\\\"><td style=\\\"padding:0cm 0cm 0cm 0cm;height:39.0pt\\\"><table class=\\\"MsoNormalTable\\\" border=\\\"0\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\" width=\\\"650\\\" style=\\\"width:487.5pt\\\"><tbody><tr style=\\\"height:56.25pt\\\"><td width=\\\"1\\\" style=\\\"width:.75pt;background:#EEEEEE;padding:0cm 0cm 0cm 0cm;height:56.25pt\\\"></td><td width=\\\"648\\\" style=\\\"width:486.0pt;background:white;padding:0cm 0cm 0cm 0cm;height:56.25pt\\\"><div align=\\\"center\\\"><table class=\\\"MsoNormalTable\\\" border=\\\"0\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\" width=\\\"430\\\" style=\\\"width:322.5pt\\\"><tbody><tr><td width=\\\"33\\\" style=\\\"width:24.75pt;padding:0cm 0cm 0cm 0cm\\\"><p class=\\\"MsoNormal\\\"><img width=\\\"24\\\" height=\\\"24\\\" id=\\\"Imagen 71\\\" alt=\\\"Descripción: Facebook:\\\" src=\\\"\\\" /></p></td><td width=\\\"215\\\" style=\\\"width:161.25pt;padding:0cm 0cm 0cm 0cm\\\"><p class=\\\"MsoNormal\\\"><span style=\\\"font-family:Arial,Helvetica,sans-serif;color:#838689;font-size:12px;font-weight:none\\\">www.bancochile.cl</span></p></td><td width=\\\"33\\\" style=\\\"width:24.75pt;padding:0cm 0cm 0cm 0cm\\\"><p class=\\\"MsoNormal\\\"><img width=\\\"24\\\" height=\\\"24\\\" id=\\\"Imagen 73\\\" alt=\\\"Descripción: Tel:\\\" src=\\\"\\\" /></p></td><td width=\\\"149\\\" style=\\\"width:111.75pt;padding:0cm 0cm 0cm 0cm\\\"><p class=\\\"MsoNormal\\\"><span style=\\\"font-size:9.0pt;font-family:'Arial','sans-serif';color:#838689\\\"> Mesa Ayuda </span><br/><b><span style=\\\"font-size:10.0pt;font-family:'Arial','sans-serif';color:#838689\\\"> 600 637 38 38 </span></b></p></td></tr></tbody></table></div></td><td width=\\\"1\\\" style=\\\"width:.75pt;background:#EEEEEE;padding:0cm 0cm 0cm 0cm;height:56.25pt\\\"></td></tr></tbody></table></td></tr><tr style=\\\"height:.75pt\\\"><td style=\\\"background:#ECECEC;padding:0cm 0cm 0cm 0cm;height:.75pt\\\"></td></tr></tbody></table></div></td></tr></tbody></table><img id=\\\"Imagen 76\\\" alt=\\\"Descripción: Tu clave es clave\\\" src=\\\"\\\" /></div><p class=\\\"MsoNormal\\\"></p></div></body></html>";
        return new TemplatesRs(file, "bch/mdp/email", "/notificacion_v3.html");
    }

    @Test
    void testGetAll_returnsList() {
        TemplatesEntity entity = getTemplatesEntity();
        when(repository.findAll()).thenReturn(List.of(entity));
        List<RpAllTmpDTO> result =  service.getAll();

        assertEquals(1, result.size());
    }

    @Test
    void testGetById_found() {
        TemplatesEntity entity = getTemplatesEntity();
        when(repository.getTemplatesByIdAndUser(anyLong())).thenReturn(entity);
        when(engineTmpRespository.getTemplateByAttr(any())).thenReturn(getTemplatesRs());

        Optional<RpIdTmpDTO> result = service.getByKey(entity.getId());

        assertTrue(result.isPresent());
        assertEquals(entity.getId(), result.get().id());
    }

    @Test
    void testGetById_throwsNoDataFoundException() {
        TemplatesEntity entity = getTemplatesEntity();
        when(repository.getTemplatesByIdAndUser(anyLong())).thenReturn(entity);
        when(engineTmpRespository.getTemplateByAttr(any())).thenThrow(new IllegalStateException());

        assertThrows(NoDataFoundException.class, () -> service.getByKey(entity.getId()));
    }


    @Test
    void validateMarkTrxStatus_existingTemplate_shouldThrowIllegalArgument() {
        // GIVEN
        String mark = "BCH";
        String trx = "8";

        // Transacción encontrada y SÍ existe plantilla previa
        TransaccionEntity trxEntity = getTransacction();
        TemplatesEntity existingTemplate = getTemplatesEntity();

        when(transaccionRepository.findByTrxIdMark(anyLong(), eq(mark))).thenReturn(trxEntity);
        when(repository.findTopByTransaccionAndMarcaOrderByIdAsc(eq(trxEntity.getDescripcion()), eq(mark))).thenReturn(existingTemplate);

        // Datos mínimos para llamada a createUpload (no se llegará tan lejos porque debe lanzar)
        TemplatesEntity entity = getTemplatesEntity();
        MultipartFile file = mock(MultipartFile.class);

        // WHEN + THEN
        assertThrows(IllegalArgumentException.class, () -> service.createUpload(entity, file, mark, trx));
    }

    @Test
    void testCreateUpload_success() throws IOException {
        TemplatesEntity entity = getTemplatesEntity();
        TmpNotificationEntity tmpNotificationEntity = getNotification();
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("/notificacion_v3.html");
        StatusEntity status = getStatusEntity();

        when(file.getBytes()).thenReturn("contenido de prueba".getBytes());

        when(transaccionRepository.findByTrxIdMark(anyLong(), anyString())).thenReturn(getTransacction());
        when(repository.findTopByTransaccionAndMarcaOrderByIdAsc(anyString(), anyString())).thenReturn(null);

        when(transaccionRepository.findByName(anyString(), anyString())).thenReturn(getTransacction());
        when(statusService.findByName(StaticGlossesUtils.POR_SER_APPROVADO)).thenReturn(status);
        when(repository.getNameMark(anyString(), anyString())).thenReturn(false);
        when(notifyRespository.findByCanalId(anyLong())).thenReturn(List.of(tmpNotificationEntity));
        //List<TmpNotificationEntity>
        when(notifyRespository.findAllWithChannelDomain())
                .thenReturn( List.of(getTmpNotificationEntityList()));

        when(file.getBytes()).thenReturn("contenido de prueba".getBytes());
        when(notifyRespository.getFolderNotifyId(anyLong())).thenReturn("folder/path");

        when(repository.save(any())).thenReturn(entity);

        Optional<TmpDTO> result = service.createUpload(entity, file, mark, transaccion);

        assertTrue(result.isPresent());
        assertEquals(entity.getName(), result.get().name());
        verify(engineTmpRespository).createEnginetmp(any(), any(MultipartFile.class));
        verify(aprovalsService).create(any());
        verify(loggerService).create(any());
    }

    @Test
    void testUpdateUpload_success() throws IOException {
        TemplatesEntity entity = getTemplatesEntity();
        MultipartFile file = mock(MultipartFile.class);
        StatusEntity status = getStatusByApproveEntity();
        TmpNotificationEntity tmpNotificationEntity = getNotification();

        when(repository.existsById(anyLong())).thenReturn(true);
        when(repository.getTemplatesByIdAndUser(anyLong())).thenReturn(entity);
        when(transaccionRepository.findByDescMark(anyString(), anyString())).thenReturn(getTransacction());
        when(statusService.findByName(StaticGlossesUtils.POR_SER_APPROVADO)).thenReturn(status);
        when(notifyRespository.findAllWithChannelDomain()).thenReturn(List.of(getTmpNotificationEntityList()));
        when(notifyRespository.findByCanalId(anyLong())).thenReturn(List.of(tmpNotificationEntity));
        when(transaccionRepository.findByName(anyString(), anyString())).thenReturn(getTransacction());
        when(file.getBytes()).thenReturn("contenido de prueba".getBytes());

        when(repository.save(any())).thenReturn(entity);

        Optional<TmpDTO> result = service.updateUpload(1L, entity, file);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().status());
        verify(aprovalsService).create(any());
        verify(loggerService).create(any());
    }

    @Test
    void testApprove_porSerAprobado_success() {
        TemplatesEntity entity = getTemplatesEntity();
        entity.setEstado(new StatusEntity(
                1L, StaticGlossesUtils.POR_SER_APPROVADO, LocalDateTime.now()));
        AprobacionesEntity aprovals = getAprovals(entity);

        when(repository.existsById(anyLong())).thenReturn(true);
        when(repository.findById(anyLong())).thenReturn(Optional.of(entity));
        when(statusService.findByName(StaticGlossesUtils.ACTIVO)).thenReturn(getStatusEntity());
        when(repository.save(any())).thenReturn(entity);

        Optional<TmpDTO> result = service.approve(1L, aprovals);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().status());
        verify(loggerService).create(any());
    }

    @Test
    void testApprove_porSerAprobado_With_Copy_success() {
        TemplatesEntity entity = getTemplatesEntity();
        entity.setName("notificacion_v3-copia.html");
        entity.setEstado(new StatusEntity(
                1L, StaticGlossesUtils.POR_SER_APPROVADO, LocalDateTime.now()));
        AprobacionesEntity aprovals = getAprovals(entity);

        when(repository.existsById(anyLong())).thenReturn(true);
        when(repository.findById(anyLong())).thenReturn(Optional.of(entity));
        when(statusService.findByName(StaticGlossesUtils.ACTIVO)).thenReturn(getStatusEntity());
        when(repository.save(any())).thenReturn(entity);

        Optional<TmpDTO> result = service.approve(1L, aprovals);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().status());
        verify(loggerService).create(any());
    }


    @Test
    void testApprove_Inactivo_success() {
        TemplatesEntity entity = getTemplatesEntity();
        entity.setEstado(new StatusEntity(
                1L, StaticGlossesUtils.INACTIVO, LocalDateTime.now()));
        AprobacionesEntity aprovals = getAprovals(entity);

        when(repository.existsById(anyLong())).thenReturn(true);
        when(repository.findById(anyLong())).thenReturn(Optional.of(entity));
        when(statusService.findByName(StaticGlossesUtils.ACTIVO)).thenReturn(getStatusEntity());
        when(repository.save(any())).thenReturn(entity);

        Optional<TmpDTO> result = service.approve(1L, aprovals);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().status());
        verify(loggerService).create(any());
    }

    @Test
    void testApprove_Activo_success() {
        TemplatesEntity entity = getTemplatesEntity();
        entity.setEstado(new StatusEntity(
                1L, StaticGlossesUtils.ACTIVO, LocalDateTime.now()));
        AprobacionesEntity aprovals = getAprovals(entity);

        when(repository.existsById(anyLong())).thenReturn(true);
        when(repository.findById(anyLong())).thenReturn(Optional.of(entity));
        when(statusService.findByName(StaticGlossesUtils.POR_SER_APPROVADO))
                .thenReturn(getStatusByApproveEntity());
        when(repository.save(any())).thenReturn(entity);

        Optional<TmpDTO> result = service.approve(1L, aprovals);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().status());
        verify(loggerService).create(any());
    }

    @Test
    void testGetByAttr_success() {
        StatusEntity statusEntity = getStatusEntity();
        TemplatesEntity entity = getTemplatesEntity();
        entity.setMarca("BCH");
        entity.setTransaccion("recaudaciones_pagos_servicios");

        TemplatesEntity expectedEntity = getTemplatesEntity();
        // Mock del query.first()
        FluentQuery.FetchableFluentQuery<TemplatesEntity> mockQuery = mock(FluentQuery.FetchableFluentQuery.class);
        when(mockQuery.first()).thenReturn(Optional.of(expectedEntity));

        when(statusService.findByName(anyString())).thenReturn(statusEntity);
        when(repository.findBy(any(Specification.class), any())).thenAnswer(invocation -> {
            Function<FluentQuery.FetchableFluentQuery<TemplatesEntity>, Object> func = invocation.getArgument(1);
            return func.apply(mockQuery);
        });

        when(engineTmpRespository.getTemplateByAttr(any())).thenReturn(getTemplatesRs());

        Optional<RpIdTmpDTO> result = service.getByAttr(entity);

        assertTrue(result.isPresent());
        assertEquals(expectedEntity.getId(), result.get().id());
    }

    @Test
    void testGetByAttr_engineError_throwsNoDataFoundException() {
        StatusEntity statusEntity = getStatusEntity();
        TemplatesEntity entity = getTemplatesEntity();
        entity.setMarca("BCH");
        entity.setTransaccion("recaudaciones_pagos_servicios");

        TemplatesEntity expectedEntity = getTemplatesEntity();

        when(statusService.findByName(anyString())).thenReturn(statusEntity);


        assertThrows(NoDataFoundException.class, () -> service.getByAttr(entity));
    }

    @Test
    void testUpdateUpload_missingUserUpdated_throwsException() {
        TemplatesEntity entity = getTemplatesEntity();
        entity.setUserUpdated("");
        when(repository.existsById(anyLong())).thenReturn(true);
        assertThrows(IllegalStateException.class, () -> service.updateUpload(1L, entity, mock(MultipartFile.class)));
    }

    @Test
    void testUpdateUpload_invalidStatus_throwsException() throws IOException {
        TemplatesEntity entity = getTemplatesEntity();
        entity.setEstado(new StatusEntity(99L, "DESCONOCIDO", LocalDateTime.now()));
        entity.setUserUpdated("usuario");
        MultipartFile file = mock(MultipartFile.class);
        // Mock existencia
        when(repository.existsById(anyLong())).thenReturn(true);
        when(repository.getTemplatesByIdAndUser(anyLong())).thenReturn(entity);
        when(transaccionRepository.findByDescMark(anyString(), anyString())).thenReturn(getTransacction());
        when(transaccionRepository.findByName(anyString(), anyString())).thenReturn(getTransacction());
        when(file.getBytes()).thenReturn("contenido de prueba".getBytes());
        when(campoRepository.findCamposByTemplateIdAndMark(anyString(), anyString())).thenReturn(List.of());
        when(notifyRespository.findAllWithChannelDomain()).thenReturn(List.of(getTmpNotificationEntityList()));

        assertThrows(IllegalStateException.class, () -> {
            service.updateUpload(1L, entity, file);
        });
    }

    @Test
    void testApprove_invalidId_throwsException() {
        when(repository.existsById(anyLong())).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> service.approve(999L, getAprovals(getTemplatesEntity())));
    }

    @Test
    void testApprove_invalidStatus_throwsException() {
        TemplatesEntity entity = getTemplatesEntity();
        entity.setEstado(new StatusEntity(99L, "DESCONOCIDO", LocalDateTime.now()));
        when(repository.existsById(anyLong())).thenReturn(true);
        when(repository.findById(anyLong())).thenReturn(Optional.of(entity));
        assertThrows(IllegalStateException.class, () -> service.approve(1L, getAprovals(entity)));
    }

    @Test
    void testCreateUpload_Voucher_success() throws IOException {
        TemplatesEntity entity = getTemplatesEntity();
        //TmpUsersEntity tmpUsersEntity = getTmpUsersEntity();
        TmpNotificationEntity tmpNotificationEntity = getNotification();
        tmpNotificationEntity.setName(StaticGlossesUtils.VOUCHER);
        StatusEntity status = getStatusEntity();
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("notificacion_v3.html");
        when(file.getBytes()).thenReturn("contenido de prueba".getBytes());

        when(transaccionRepository.findByName(anyString(), anyString())).thenReturn(getTransacction());

        when(transaccionRepository.findByTrxIdMark(anyLong(), anyString())).thenReturn(getTransacction());
        when(repository.findTopByTransaccionAndMarcaOrderByIdAsc(anyString(), anyString())).thenReturn(null);
        when(statusService.findByName(anyString())).thenReturn(status);
        when(repository.getNameMark(anyString(), anyString())).thenReturn(false);
        when(notifyRespository.findByCanalId(anyLong())).thenReturn(List.of(tmpNotificationEntity));
        when(notifyRespository.findAllWithChannelDomain()).thenReturn(
                List.of(getTmpNotificationEntityList()));
        when(notifyRespository.findById(anyLong())).thenReturn(Optional.of(tmpNotificationEntity));
        when(signaturesService.getSignatureActive()).thenReturn(sampleSignature());
        when(notifyRespository.getFolderNotifyId(anyLong())).thenReturn("folder/path");
        when(repository.save(any())).thenReturn(entity);

        Optional<TmpDTO> result = service.createUpload(entity, file, mark, transaccion);

        assertTrue(result.isPresent());
    }

    @Test
    void testUpdateUpload_Activo_success() throws IOException {
        TemplatesEntity entity = getTemplatesEntity();
        entity.setEstado(new StatusEntity(1L, StaticGlossesUtils.ACTIVO, LocalDateTime.now()));
        //TmpUsersEntity tmpUsersEntity = getTmpUsersEntity();
        TmpNotificationEntity tmpNotificationEntity = getNotification();
        StatusEntity status = getStatusByApproveEntity();
        MultipartFile file = mock(MultipartFile.class);

        when(repository.existsById(anyLong())).thenReturn(true);
        when(statusService.findByName(StaticGlossesUtils.POR_SER_APPROVADO)).thenReturn(status);
        when(transaccionRepository.findByDescMark(anyString(), anyString())).thenReturn(getTransacction());
        when(notifyRespository.findByCanalId(anyLong())).thenReturn(List.of(tmpNotificationEntity));
        when(notifyRespository.findAllWithChannelDomain()).thenReturn(
                List.of(getTmpNotificationEntityList()));
        when(repository.getTemplatesByIdAndUser(anyLong())).thenReturn(entity);

        when(transaccionRepository.findByName(anyString(), anyString())).thenReturn(getTransacction());
        when(file.getBytes()).thenReturn("contenido de prueba".getBytes());

        when(repository.save(any())).thenReturn(entity);

        Optional<TmpDTO> result = service.updateUpload(1L, entity, file);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().status());
        verify(aprovalsService).create(any());
        verify(loggerService).create(any());
    }

    @Test
    void testGetById_throwsIlegalActionException () {
        TemplatesEntity entity = getTemplatesEntity();
        when(repository.getTemplatesByIdAndUser(anyLong())).thenReturn(entity);
        when(engineTmpRespository.getTemplateByAttr(any())).thenThrow(new IlegalActionException("Error"));

        assertThrows(IlegalActionException.class, () -> service.getByKey(entity.getId()));
    }


    @Test
    public void testVerifyNotificationById_NotFound_ShouldThrowException() {

        NotificacionDTO notificacion = new NotificacionDTO();
        notificacion.setId(999L);

        CanalDTO canal = new CanalDTO();
        canal.setNotificaciones(List.of(notificacion));

        DominioDTO dominio = new DominioDTO();
        dominio.setCanales(List.of(canal));

        List<DominioDTO> dominios = List.of(dominio);

        Long idBuscado = 123L; // Este ID no está en la lista

        Assertions.assertThrows(IlegalActionException.class, () -> {
            service.verifyNotificationById(dominios, idBuscado);
        });
    }


//    @Test
//    public void testVerifyStatusByName_Exists_ShouldThrowException() {
//        TemplatesEntity entity = new TemplatesEntity();
//        entity.setName("SolicitudX");
//
//        StatusEntity status = new StatusEntity();
//        status.setName("Pendiente");
//
//        StatusEntity byInactive = new StatusEntity();
//        status.setName("Inactivo");
//
//        Mockito.when(repository.getByNameStatus(anyString(), anyLong()))
//                .thenReturn(true);
//
//        Assertions.assertThrows(PotentialStubbingProblem.class, () -> {
//            service.verifyStatusByName(entity, status, byInactive);
//        });
//    }
    @Test
    public void testVerifyByNameMark_Exists_ShouldThrowException() {
        TemplatesEntity entity = new TemplatesEntity();
        entity.setName("SolicitudX");
        String mark = "BCH";
        Mockito.when(repository.getNameMark(anyString(), anyString()))
                .thenReturn(true);

        Assertions.assertThrows(IlegalActionException.class, () -> {
            service.verifyByNameMark(entity, mark);
        });
    }



    @Test
    public void testVerifyStatusByNameFolder_Exists_ShouldThrowException() {
        TemplatesEntity entity = new TemplatesEntity();
        entity.setName("SolicitudX");
        entity.setFolder("CarpetaA");

        StatusEntity status = new StatusEntity();
        status.setId(1L);
        status.setName("Pendiente");
        entity.setEstado(status);

        Mockito.when(repository.getByNameStatus("SolicitudX", 1L, "CarpetaA"))
                .thenReturn(true);

        Assertions.assertThrows(IlegalActionException.class, () -> {
            service.verifyStatusByNameFolder(entity);
        });
    }

    @Test
    void testObtenerTransaccionesPorMarca_returnsList() {
        TransaccionEntity entity = getTransacction();
        when(transaccionRepository.findByMarca_Id(anyString())).thenReturn(List.of(entity));
        List<TransaccionDTO> result =  service.obtenerTransaccionesPorMarca("1L");
        assertEquals(1, result.size());
    }

    @Test
    void obtenerTransaccionesPorMarca_deberiaRetornarListaDeDTOs() {

        String marcaId = "M123";

        MarcaEntity marca = new MarcaEntity();
        marca.setId(marcaId);

        TransaccionEntity transaccion1 = new TransaccionEntity();
        transaccion1.setId(1L);
        transaccion1.setDescripcion("Compra supermercado");
        transaccion1.setMarca(marca);
        transaccion1.setNombre("Transacción 1");

        TransaccionEntity transaccion2 = new TransaccionEntity();
        transaccion2.setId(2L);
        transaccion2.setDescripcion("Pago servicio");
        transaccion2.setMarca(marca);
        transaccion2.setNombre("Transacción 2");

        List<TransaccionEntity> mockTransacciones = List.of(transaccion1, transaccion2);

        Mockito.when(transaccionRepository.findByMarca_Id(marcaId)).thenReturn(mockTransacciones);

        List<TransaccionDTO> resultado = service.obtenerTransaccionesPorMarca(marcaId);

        Assertions.assertEquals(2, resultado.size());
        Assertions.assertEquals("Transacción 1", resultado.get(0).nombre());
        Assertions.assertEquals("Transacción 2", resultado.get(1).nombre());
        Assertions.assertEquals(marcaId, resultado.get(0).marcaId());
    }

    @Test
    void getAllMarcas_deberiaRetornarListaDeMarcaDTO() {

        MarcaEntity marca1 = new MarcaEntity();
        marca1.setId("M001");
        marca1.setNombre("Marca Uno");

        MarcaEntity marca2 = new MarcaEntity();
        marca2.setId("M002");
        marca2.setNombre("Marca Dos");

        List<MarcaEntity> mockMarcas = List.of(marca1, marca2);

        Mockito.when(marcaRepository.findAll()).thenReturn(mockMarcas);

        List<MarcaDTO> resultado = service.getAllMarcas();

        Assertions.assertEquals(2, resultado.size());
        Assertions.assertEquals("M001", resultado.get(0).id());
        Assertions.assertEquals("Marca Uno", resultado.get(0).nombre());
        Assertions.assertEquals("M002", resultado.get(1).id());
        Assertions.assertEquals("Marca Dos", resultado.get(1).nombre());
    }

    @Test
    void testSaveAndDeleteEntity_withVersion_shouldInactivatePrevious() {
        // Entidad actual con versión v3
        TemplatesEntity entity = getTemplatesEntity();
        entity.setId(1L);
        entity.setName("notificacion-v3.html");
        entity.setFolder("bch/mdp/email");
        entity.setEstado(new StatusEntity(3L, StaticGlossesUtils.POR_SER_APPROVADO, LocalDateTime.now()));

        // Entidad anterior con versión v2
        TemplatesEntity previousVersion = getTemplatesEntity();
        previousVersion.setName("notificacion-v2.html");
        previousVersion.setFolder("bch/mdp/email");
        previousVersion.setEstado(getStatusEntity());

        // Estados
        StatusEntity activo = getStatusEntity();
        StatusEntity inactivo = new StatusEntity(2L, StaticGlossesUtils.INACTIVO, LocalDateTime.now());

        // Mocks necesarios
        when(repository.existsById(entity.getId())).thenReturn(true);
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));
        when(statusService.findByName(StaticGlossesUtils.ACTIVO)).thenReturn(activo);
        when(statusService.findByName(StaticGlossesUtils.INACTIVO)).thenReturn(inactivo);
        when(repository.getEntityByNameStatusFolder("notificacion-v2.html", activo.getId(), entity.getFolder()))
                .thenReturn(previousVersion);
        when(repository.save(any())).thenReturn(entity);

        // Ejecutar
        Optional<TmpDTO> result = service.approve(entity.getId(), getAprovals(entity));

        // Verificaciones
        assertTrue(result.isPresent());
        verify(repository).save(previousVersion); // se inactiva la versión anterior
        verify(repository, times(3)).save(any()); // se guarda anterior y actual (2 veces)
    }

    @Test
    void testSaveAndDeleteEntity_withoutVersion_shouldSaveDirectly() {
        TemplatesEntity entity = getTemplatesEntity();
        entity.setName("notificacion.html");
        StatusEntity activo = getStatusEntity();

        when(repository.existsById(entity.getId())).thenReturn(true);
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));
        when(statusService.findByName(StaticGlossesUtils.ACTIVO)).thenReturn(activo);
        when(repository.save(any())).thenReturn(entity);

        Optional<TmpDTO> result = service.approve(entity.getId(), getAprovals(entity));

        assertTrue(result.isPresent());
        verify(repository, times(2)).save(any());
    }

}