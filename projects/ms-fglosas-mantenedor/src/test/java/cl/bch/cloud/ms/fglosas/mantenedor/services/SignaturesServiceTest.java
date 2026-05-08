package cl.bch.cloud.ms.fglosas.mantenedor.services;

import cl.bch.cloud.ms.fglosas.mantenedor.dtos.AprovalsDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.RequestSignatureSingsysDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.ResponseSignatureSingSysDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.SignaturesDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.*;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.IlegalActionException;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.NoDataFoundException;
import cl.bch.cloud.ms.fglosas.mantenedor.repositories.SignaturesRepository;
import cl.bch.cloud.ms.fglosas.mantenedor.repositories.SignsysRepository;
import cl.bch.cloud.ms.fglosas.mantenedor.utils.StaticGlossesUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignaturesServiceTest {

    private SignaturesRepository repository;
    private StatusService statusService;
    private LogMantenedorService loggerService;
    private AprovalsService aprovalsService;
    private SignsysRepository singSysRepository;
    private SignaturesService service;

    @BeforeEach
    void setUp() {
        repository = mock(SignaturesRepository.class);
        statusService = mock(StatusService.class);
        loggerService = mock(LogMantenedorService.class);
        aprovalsService = mock(AprovalsService.class);
        singSysRepository = mock(SignsysRepository.class);
        service = new SignaturesService(repository, statusService, loggerService,
                aprovalsService, singSysRepository);
    }

    private SignaturesEntity sampleSignature() {

        SignaturesEntity dto = new SignaturesEntity(
                1L, "16742032-k", "Test Signature", "admin",
                "KJJDKKFDJ", getStatusEntity(), LocalDateTime.now());
        return dto;
    }

    protected TypeGlossesEntity getTypeGlosses() {
        return  new TypeGlossesEntity(1L, StaticGlossesUtils.IDENTITY_GLOSSES,  LocalDateTime.now());
    }
    protected StatusEntity getStatusEntity() {
        return new StatusEntity(1L, StaticGlossesUtils.APPROVADO, LocalDateTime.now());
    }

    AprovalsDTO sampleAprovalsDTO() {
        return new AprovalsDTO(2L, StaticGlossesUtils.IDENTITY_GLOSSES,
                1L, "SYSTEM", "OK", LocalDateTime.now(), LocalDateTime.now());
    }

    String getToken() {
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1bmlxdWVfbmFtZSI6ImZnbG9zYXMiLCJBcHBWZXJzaW9uIjo" +
                "ic3RyaW5nIiwiQXBwSWQiOiJzdHJpbmciLCJBdWRpZW5jZVRva2VuR2VuZXJhdGVkIjoiNWRuOUxENWxqMm13M0NsSkxNaFMiLCJJ" +
                "c3N1ZXJUb2tlbkdlbmVyYXRlZCI6IjVkbjlMRDVsajJtdzNDbEpMTWhTIiwicm9sZSI6WyJGQEZ1bGxBUEkiLCJGQExlZ2FsQX" +
                "V0aG9yaXplciIsIkZATGVnYWxTdXBlcnZpc29yIl0sIm5iZiI6MTc1MjUxMDQ3MSwiZXhwIjoxNzUyNTEyMjcxLCJpYXQiOjE3" +
                "NTI1MTA0NzEsImlzcyI6IjVkbjlMRDVsajJtdzNDbEpMTWhTIiwiYXVkIjoiNWRuOUxENWxqMm13M0NsSkxNaFMifQ.BvqMvIR" +
                "IIKDQJps365ze3Um5I4FkLBIJf8Oocn-KscQ";
    }

    private ResponseSignatureSingSysDTO getResponseSignatureSingSys() {
        String img = "iVBORw0KGgoAAAANSUhEUgAAAU0AAAB1AQMAAADwYCHzAAAABGdBTUEAALGPC/xhBQAAAAZQTFRFAAAA////pdmf3QAAA" +
                "AlwSFlzAAAOwgAADsIBFShKgAAAA3NJREFUWMPt10Fr1EAUB/CU1q6gsoKXKmoUD+JJRNCqS0fxg6hQ2qOCJ2npWC0sSqHgWTr" +
                "H4ifw4GFXWqqINhdBD8JuXWn0YlYiJmMm83xJVsh2s/PWi6fNocnhR3bIvP97UwsGvoZ0SId0SIf0v1AO0EjugqSadxQjqe" +
                "IdRdPmwFTvtdM1ZH+MVF0qg/67EDMNpdOhQNGW70KcPEiSvgoGpbET+Bn9QVFpt3yIkiefoiHzOqZFUu4kFBfqkhQaEQQJX" +
                "aGoBCGzt5LUh5oEL3myKdoCJtNy1UU0zFMXeEZVUWXLPHV0h0qWfDnTAqoa9yp5oc8hLFUHojsc6vy5iZ5VoOwaLmGLF32D" +
                "HNW3YyxUju89Ck0gqMyoqsC6mSovwp3i+HFjKoYhFovEAPJ12S6Q+S2QKcW0luQ6QaNGAArpPI8YQX2kYQV0HdqcoEHDhf" +
                "AYqNEsBIGBug0P5EvMAlQp6ggPoqY1Auo0TR2IQqTtNFqugTZqDYgiTLfrUFQkFBuBtlPqmCgTEAXgh1xQtMZqCcVmVEvXY" +
                "6CMM4hccEtZsQiC+h7s2Bmt9ad6gXMIPPiajqTecdBFsQBHHaxqzeOCcZCnWvOmdQN3dSBaumBjAHXStnrGQZ4q3Z4qLSob1" +
                "PYc6+3xXVRt6xVLCT1P0jh2YlG5J/SBL3dZT2F1U3XcdWYOr1ZGviJ9ZaThvLs1u//4pfNvkK6YqJTKW60vTz27W7csxkx0" +
                "rhk7q/WTFl4782PcQGHufrz09MG5E9bM1U3e27Ty9MXou5HK43PW1S/jB3nLSMNS/VrlyLRljS3cYSUjbVc3rk0emZ4YK2Mr" +
                "Kxvp5w/vNycvT4vvGJWCVpSnS9WlxSuTt8BxCo8Z3XTPo/NIhUjGrYnqU2tnnlw8ikkRhVM+R8PW2r7xi4uYFExV1Uzdmevjs" +
                "y8zWjZS2Nm6vt9LksIKD095uvFpwnIyWjSL8vTtx4mDyXfCbD8k6LtDjVgkJwceUrNg+RCkFDNOzYKn1b90EQgq1tKt9/UdRl" +
                "LsmfjLri4+f++iv1M6StIyg194cyxO0lkGP/H2epykukPvT9H0B4dtlGWgKZbzN2iPiAGoKsO6suzCQ143VXAT+wovPIvsojEs" +
                "WJYNfX5+F+Vwus9/Dz00y8kAFDu6vgH9rxyN+pxb+1A2GA3+jfL+EsI/oIYfCYyGLXwAAAAASUVORK5CYII=";

        ResponseSignatureSingSysDTO response = new ResponseSignatureSingSysDTO(
                "CLTConvert", "2023-06-09T22:20:54.43", 645124, 0, 663703,
                0, "CLT/0100914468", "2013-06-04T00:00:00", "CLT/0100914468",
                "2013-06-04T00:00:00", img, null, null,
                null, null, null,
                null, null, null, null);

        return response;
    }

    private RequestSignatureSingsysDTO getRequest() {
        RequestSignatureSingsysDTO dto = new RequestSignatureSingsysDTO(
                0, "10329642-0", "0", getToken() );
        return dto;
    }


    @Test
    void testGetAll_returnsList() {
        SignaturesEntity entity = sampleSignature();
        when(repository.findAll()).thenReturn(List.of(entity));

        List<SignaturesDTO> result =  service.getAll();

        assertEquals(1, result.size());
    }
    @Test
    void testGetById_found() {
        SignaturesEntity entity = sampleSignature();

        when(repository.findById(anyLong())).thenReturn(Optional.of(entity));

        Optional<SignaturesDTO> result = service.getById(anyLong());

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().id());
    }

    @Test
    void testGetById_exception() {
        when(repository.findById(1L)).thenThrow(new RuntimeException("DB error"));
        assertThrows(NoDataFoundException.class, () -> service.getById(1L));
    }

    @Test
    void testCreate_success() {
        RequestSignatureSingsysDTO request = getRequest();
        ResponseSignatureSingSysDTO response = getResponseSignatureSingSys();

        SignaturesEntity entity = sampleSignature();
        entity.setRut("10329642-0");
        entity.setUserCreated("user");

        when(repository.exists(any())).thenReturn(false);
        when(statusService.findByName(StaticGlossesUtils.POR_SER_APPROVADO))
                .thenReturn(getStatusEntity());
        when(singSysRepository.connect()).thenReturn(getToken());
        when(singSysRepository.getSignature(any(RequestSignatureSingsysDTO.class)))
                .thenReturn(response);
        when(repository.save(any())).thenReturn(entity);

        Optional<SignaturesDTO> result = service.create(entity);

        assertTrue(result.isPresent());
        verify(aprovalsService).create(any());
        verify(loggerService).create(any());
    }

    @Test
    void testCreate_duplicateRut_throwsException() {
        SignaturesEntity entity = new SignaturesEntity();
        entity.setRut("12345678-9");

        when(repository.exists(any())).thenReturn(true);

        assertThrows(IlegalActionException.class, () -> service.create(entity));
    }

    @Test
    void testDelete_success() {
        SignaturesEntity entity = sampleSignature();
        StatusEntity statusEntity = getStatusEntity();
        statusEntity.setName(StaticGlossesUtils.ACTIVO);

        when(repository.existsById(anyLong())).thenReturn(true);
        when(statusService.findByName(StaticGlossesUtils.ACTIVO))
                .thenReturn(statusEntity);
        when(repository.findAllWithStatus(anyLong())).thenReturn(List.of(entity));
        when(repository.findById(anyLong())).thenReturn(Optional.of(entity));

        boolean result = service.delete(entity.getId(), sampleAprovalsDTO().toEntity());

        assertTrue(result);
        verify(repository).deleteById(1L);
        verify(loggerService).create(any());
    }
    @Test
    void testDelete_notFound() {
        SignaturesEntity entity = sampleSignature();
        StatusEntity statusEntity = getStatusEntity();
        statusEntity.setName(StaticGlossesUtils.ACTIVO);

        when(repository.existsById(anyLong())).thenReturn(false);
        boolean result = service.delete(entity.getId(), sampleAprovalsDTO().toEntity());
        assertFalse(result);
    }

    @Test
    void testToggleStatus_activeToInactive() {
        SignaturesEntity entity = new SignaturesEntity();
        entity.setEstadoId(new StatusEntity(1L, StaticGlossesUtils.ACTIVO, LocalDateTime.now()));

        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(statusService.findByName(StaticGlossesUtils.INACTIVO)).thenReturn(new StatusEntity());
        Long[] ids = {1L};
        boolean result = service.toggleStatus(ids, new AprobacionesEntity());

        assertTrue(result);
        verify(loggerService).create(any());
    }

    @Test
    void testToggleStatus_InactiveToActive() {
        SignaturesEntity entity = new SignaturesEntity();
        entity.setEstadoId(new StatusEntity(1L, StaticGlossesUtils.INACTIVO, LocalDateTime.now()));

        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(statusService.findByName(StaticGlossesUtils.ACTIVO)).thenReturn(new StatusEntity());

        boolean result = service.toggleStatus(1L, new AprobacionesEntity());

        assertTrue(result);
        verify(loggerService).create(any());
    }

    @Test
    void testToggleStatus_with_existsById() {
        SignaturesEntity entity = new SignaturesEntity();
        entity.setEstadoId(new StatusEntity(1L, "DESCONOCIDO", LocalDateTime.now()));

        when(repository.existsById(1L)).thenReturn(false);

        assertThrows(NoDataFoundException.class, () -> service.toggleStatus(1L, new AprobacionesEntity()));

    }

    @Test
    void testToggleStatus_with_cero_entity() {
        SignaturesEntity entity = new SignaturesEntity();
        entity.setEstadoId(new StatusEntity(1L, "DESCONOCIDO", LocalDateTime.now()));

        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(anyLong())).thenReturn(Optional.of(entity));

        assertThrows(IllegalStateException.class, () -> service.toggleStatus(1L, new AprobacionesEntity()));

    }

    @Test
    void testApprove_success() {
        SignaturesEntity entity = sampleSignature();
        entity.setEstadoId(new StatusEntity(1L, StaticGlossesUtils.POR_SER_APPROVADO, LocalDateTime.now()));

        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(statusService.findByName(StaticGlossesUtils.ACTIVO))
                .thenReturn(getStatusEntity());

        Optional<SignaturesDTO> result = service.approve(1L, sampleAprovalsDTO().toEntity());

        assertTrue(result.isPresent());
        verify(loggerService).create(any());
    }

    @Test
    void testApprove_invalidStatus_throwsException() {
        SignaturesEntity entity = new SignaturesEntity();
        entity.setEstadoId(new StatusEntity(1L, "DESCONOCIDO", LocalDateTime.now()));

        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        assertThrows(IllegalStateException.class, () -> service.approve(1L, new AprobacionesEntity()));
    }

    @Test
    void testUpdate_success() {
        RequestSignatureSingsysDTO request = getRequest();
        ResponseSignatureSingSysDTO response = getResponseSignatureSingSys();
        Long id = 1L;
        String userUpdated = "Tester";

        when(repository.existsById(anyLong())).thenReturn(true);
        when(repository.findById(sampleSignature().getId())).thenReturn(Optional.of(sampleSignature()));
        when(singSysRepository.connect()).thenReturn(getToken());
        when(singSysRepository.getSignature(any(RequestSignatureSingsysDTO.class))).thenReturn(response);
        when(repository.save(any())).thenReturn(sampleSignature());
        Optional<SignaturesDTO> result = service.update(id, userUpdated);

        assertNotNull(result);
        assertEquals(sampleSignature().getName(), result.get().name());
        verify(repository).findById(sampleSignature().getId());
    }

    @Test
    void testUpdate_notFound() {
        Long id = 99L;
        String userUpdated = "Tester";
        when(repository.existsById(anyLong())).thenReturn(true);
        when(repository.findById(id)).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> service.update(id, userUpdated));
        verify(repository).findById(id);
    }

    @Test
    public void testSignatureACtive() {
        when(statusService.findByName(StaticGlossesUtils.ACTIVO))
                .thenReturn(getStatusEntity());
        when(repository.findAllWithStatus(anyLong())).thenReturn(Arrays.asList(sampleSignature()));
        SignaturesEntity entity = service.getSignatureActive();
        assertNotNull(entity);
    }

    @Test
    void testEnable_success() {
        Long id = 1L;
        SignaturesEntity entity = sampleSignature();
        entity.setEstadoId(new StatusEntity(1L, StaticGlossesUtils.INACTIVO, LocalDateTime.now()));

        when(repository.existsById(id)).thenReturn(true);
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(statusService.findByName(StaticGlossesUtils.ACTIVO)).thenReturn(getStatusEntity());

        boolean result = service.enable(id, sampleAprovalsDTO().toEntity());

        assertTrue(result);
        verify(repository).save(any(SignaturesEntity.class));
        verify(loggerService).create(any());
    }

    @Test
    void testEnable_idNotFound_throwsException() {
        Long id = 99L;
        when(repository.existsById(id)).thenReturn(false);

        assertThrows(NoDataFoundException.class, () -> service.enable(id, new AprobacionesEntity()));
    }

    @Test
    void testEnable_invalidStatus_throwsException() {
        Long id = 1L;
        SignaturesEntity entity = sampleSignature();
        entity.setEstadoId(new StatusEntity(1L, "DESCONOCIDO", LocalDateTime.now()));

        when(repository.existsById(id)).thenReturn(true);
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        assertThrows(IllegalStateException.class, () -> service.enable(id, new AprobacionesEntity()));
    }

    @Test
    void testToggleStatus_multipleIds_oneFails_shouldThrowException() {
        Long[] ids = {1L, 2L};

        SignaturesEntity entity1 = sampleSignature();
        entity1.setEstadoId(new StatusEntity(1L, StaticGlossesUtils.ACTIVO, LocalDateTime.now()));

        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(entity1));
        when(statusService.findByName(StaticGlossesUtils.INACTIVO)).thenReturn(getStatusEntity());

        when(repository.existsById(2L)).thenReturn(false); // Simula fallo en el segundo ID

        assertThrows(NoDataFoundException.class, () -> {
            service.toggleStatus(ids, new AprobacionesEntity());
        });
    }

}