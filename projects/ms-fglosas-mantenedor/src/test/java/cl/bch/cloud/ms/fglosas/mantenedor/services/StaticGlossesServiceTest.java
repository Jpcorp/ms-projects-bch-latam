package cl.bch.cloud.ms.fglosas.mantenedor.services;

import cl.bch.cloud.ms.fglosas.mantenedor.dtos.AprovalsDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.LoggerMaintainerDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.StaticGlossesDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.StaticGlossesEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.NoDataFoundException;
import cl.bch.cloud.ms.fglosas.mantenedor.repositories.StaticGlossesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

import cl.bch.cloud.ms.fglosas.mantenedor.entities.*;
import cl.bch.cloud.ms.fglosas.mantenedor.utils.StaticGlossesUtils;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class StaticGlossesServiceTest {

    private StaticGlossesRepository repository;
    private AprovalsService aprovalsService;
    private LogMantenedorService loggerService;
    private StatusService statusService;
    private TypeGlossesService typeService;
    private StaticGlossesService service;
    private StaticGlossesEntity entity;
    private StatusEntity toBeApproved;
    private StatusEntity approved;

    @BeforeEach
    void setUp() {
        repository = mock(StaticGlossesRepository.class);
        aprovalsService = mock(AprovalsService.class);
        loggerService = mock(LogMantenedorService.class);
        statusService = mock(StatusService.class);
        typeService = mock(TypeGlossesService.class);
        service = new StaticGlossesService(repository, aprovalsService, loggerService, statusService, typeService);
        entity = getGlossesEntity();
        toBeApproved = new StatusEntity(
                2L, StaticGlossesUtils.POR_SER_APPROVADO, LocalDateTime.now());
        approved = new StatusEntity(
                3L, StaticGlossesUtils.APPROVADO, LocalDateTime.now());
    }
    protected StaticGlossesEntity getGlossesEntity() {
        return new StaticGlossesEntity(1L, "NAME", "VALUE", "userCreated",
                "UserUpdate", getStatusEntity(), getTypeGlosses(), LocalDateTime.now(),
                LocalDateTime.now());
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

    @Test
    void testGetGlossesById_found() {

        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        Optional<StaticGlossesDTO> result = service.getGlossesById(1L);

        assertTrue(result.isPresent());
        assertEquals("NAME", result.get().name());
    }

    @Test
    void testGetAllGlosses() {
        when(repository.findAll()).thenReturn(List.of(entity));

        List<StaticGlossesDTO> result = service.getAllGlosses();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("NAME", result.get(0).name());
    }

    @Test
    void testCreate_success() {

        StaticGlossesEntity param = new StaticGlossesEntity(3L, "NAME 2", "VALUE", "userCreated",
                "UserUpdate", getStatusEntity(), getTypeGlosses(),
                LocalDateTime.now(), LocalDateTime.now());

        when(statusService.findByName(
                StaticGlossesUtils.POR_SER_APPROVADO))
                .thenReturn(getStatusEntity());

        when(typeService.findByName(
                StaticGlossesUtils.IDENTITY_GLOSSES))
                .thenReturn(getTypeGlosses());

        when(repository.findAll((Specification<StaticGlossesEntity>) any()))
                .thenReturn(List.of(param));

        Optional<StaticGlossesDTO> result = service.create(entity);

        assertTrue(result.isPresent());
        verify(aprovalsService).create(any());
        verify(loggerService).create(any());
    }

    @Test
    void testUpdate_notFound() {
        when(repository.existsById(1L)).thenReturn(false);

        Optional<StaticGlossesDTO> result = service.update(1L, new StaticGlossesEntity());

        assertTrue(result.isEmpty());
    }

    @Test
    void testDelete_success() {
        StaticGlossesEntity entity = new StaticGlossesEntity();
        entity.setId(1L);
        entity.setName("Glosa");
        entity.setValue("value");

        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(statusService.findByName(StaticGlossesUtils.POR_SER_APPROVADO))
                .thenReturn(new StatusEntity(2L, StaticGlossesUtils.POR_SER_APPROVADO, LocalDateTime.now()));
        when(repository.existsByName("Glosa", 2L)).thenReturn(true);

        boolean result = service.delete(1L, sampleAprovalsDTO().toEntity());

        assertTrue(result);
        verify(repository).deleteById(1L);
        verify(loggerService).create(any());
    }

    @Test
    void testDelete_false() {
        when(repository.existsById(1L)).thenReturn(false);
        assertFalse(service.delete(1L, sampleAprovalsDTO().toEntity()));
    }

    @Test
    void testGlossesAproved_exception() {
        Long glossId = 1L;

        AprobacionesEntity inputApproval = new AprobacionesEntity();
        inputApproval.setId(glossId);
        inputApproval.setIdentity(StaticGlossesUtils.IDENTITY_GLOSSES);
        inputApproval.setIdentityId(glossId);
        inputApproval.setChecker("checker");
        inputApproval.setComentario("comentario");
        inputApproval.setFechaAprobacion(LocalDateTime.now());

        when(repository.existsById(glossId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.glossesAproved(glossId, inputApproval));
    }

    @Test
    void testGlossesAproved_success() {
        Long glossId = 1L;
        AprobacionesEntity inputApproval = new AprobacionesEntity();
        inputApproval.setId(glossId);
        inputApproval.setIdentity(StaticGlossesUtils.IDENTITY_GLOSSES);
        inputApproval.setIdentityId(glossId);
        inputApproval.setChecker("checker");
        inputApproval.setComentario("comentario");
        inputApproval.setFechaAprobacion(LocalDateTime.now());

        AprobacionesEntity approvalFromDB = new AprobacionesEntity();
        approvalFromDB.setIdentityId(glossId);

        StaticGlossesEntity glossToApprove = entity;

        // Solo una glosa aprobada en la lista
        List<StaticGlossesEntity> approvedGlosses = List.of(); // ← Lista vacía para que no lance excepción

        when(repository.existsById(glossId)).thenReturn(true);
        when(statusService.findByName(StaticGlossesUtils.ACTIVO)).thenReturn(toBeApproved); // ACTIVO puede ser otro StatusEntity si lo necesitas
        when(aprovalsService.findByIdentityWithStatus(StaticGlossesUtils.IDENTITY_GLOSSES, glossId))
                .thenReturn(approvalFromDB);
        when(repository.findById(glossId)).thenReturn(Optional.of(glossToApprove));
        when(statusService.findByName(StaticGlossesUtils.APPROVADO)).thenReturn(approved);
        when(repository.findAll((Specification<StaticGlossesEntity>) any())).thenReturn(approvedGlosses);
        when(repository.save(any())).thenReturn(glossToApprove);

        Optional<StaticGlossesDTO> result = service.glossesAproved(glossId, inputApproval);

        assertTrue(result.isPresent());
        assertEquals("NAME", result.get().name());
        verify(repository).save(glossToApprove);
    }

    @Test
    void testGlossesAproved_With_Criteria_success() {
        Long glossId = 1L;
        AprobacionesEntity inputApproval = new AprobacionesEntity();
        inputApproval.setId(glossId);
        inputApproval.setIdentity(StaticGlossesUtils.IDENTITY_GLOSSES);
        inputApproval.setIdentityId(glossId);
        inputApproval.setChecker("checker");
        inputApproval.setComentario("comentario");
        inputApproval.setFechaAprobacion(LocalDateTime.now());

        AprobacionesEntity approvalFromDB = new AprobacionesEntity();
        approvalFromDB.setIdentityId(glossId);

        StaticGlossesEntity glossToApprove = entity;

        approved.setId(1L);
        glossToApprove.setName("");
        // Solo una glosa aprobada en la lista
        List<StaticGlossesEntity> approvedGlosses = List.of(); // ← Lista vacía para que no lance excepción

        when(repository.existsById(glossId)).thenReturn(true);
        when(statusService.findByName(StaticGlossesUtils.ACTIVO)).thenReturn(toBeApproved); // ACTIVO puede ser otro StatusEntity si lo necesitas
        when(aprovalsService.findByIdentityWithStatus(StaticGlossesUtils.IDENTITY_GLOSSES, glossId))
                .thenReturn(approvalFromDB);

        when(repository.findById(glossId)).thenReturn(Optional.of(glossToApprove));
        when(statusService.findByName(StaticGlossesUtils.APPROVADO)).thenReturn(approved);

        when(repository.findAll((Specification<StaticGlossesEntity>) any())).thenReturn(approvedGlosses);
        when(repository.save(any())).thenReturn(glossToApprove);

        Optional<StaticGlossesDTO> result = service.glossesAproved(glossId, inputApproval);

        assertTrue(result.isPresent());
        assertEquals("", result.get().name());
        verify(repository).save(glossToApprove);
    }

    @Test
    void testGlossesUpdate_success() {
        //registro por actualizar
        StaticGlossesEntity update =
                new StaticGlossesEntity(1L, "NAME", "VALUE 2", "userCreated",
                "UserUpdate", getStatusEntity(), getTypeGlosses(), LocalDateTime.now(),
                LocalDateTime.now());

        AprobacionesEntity authorized = StaticGlossesUtils
                .createAuthorized(update, "");

        LogMantenedorEntity log = StaticGlossesUtils.createLogEntity(update,
                StaticGlossesUtils.UPDATED, "VALUE_OLD");

        // Stubbing para el estado "Por ser aprobado"
        when(statusService.findByName(StaticGlossesUtils.POR_SER_APPROVADO))
                .thenReturn(toBeApproved);
        //existe id
        when(repository.existsById(anyLong())).thenReturn(true);

        when(repository.existsByName(anyString(), anyLong()))
                .thenReturn(false);

        when(repository.findById(anyLong())).thenReturn(Optional.of(entity));

        when(repository.save(any())).thenReturn(entity);

        when(aprovalsService.create(any())).thenReturn(
                Optional.of(AprovalsDTO.fromEntity(authorized)));

        when(loggerService.create(any())).thenReturn(
                Optional.of(LoggerMaintainerDTO.fromEntity(log)));

        // Ejecución del método y validaciones
        Optional<StaticGlossesDTO> result = service.update(update.getId(), update);

        assertTrue(result.isPresent());
        assertEquals("NAME", result.get().name());
        verify(repository).save(entity);
    }

    @Test
    void testGlossesUpdate_exception() {
        Long glossId = 1L;
        when(repository.existsById(anyLong())).thenReturn(true);
        when(statusService.findByName(anyString())).thenReturn(toBeApproved);
        when(repository.existsByName(anyString(), anyLong())).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> service.update(glossId, entity));
    }

    @Test
    void testToggleStatus_idNotExists_returnsFalse() {
        when(repository.existsById(1L)).thenReturn(false);
        boolean result = service.toggleStatus(1L, new AprobacionesEntity());
        assertFalse(result);
    }

    @Test
    void testToggleStatus_activeToInactive_success() {
        entity.setEstadoId(new StatusEntity(1L, StaticGlossesUtils.ACTIVO, LocalDateTime.now()));
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(statusService.findByName(StaticGlossesUtils.INACTIVO)).thenReturn(toBeApproved);
        when(repository.save(any())).thenReturn(entity);

        boolean result = service.toggleStatus(1L, sampleAprovalsDTO().toEntity());
        assertTrue(result);
        verify(loggerService).create(any());
    }

    @Test
    void testToggleStatus_inactiveToActive_success() {
        entity.setEstadoId(new StatusEntity(1L, StaticGlossesUtils.INACTIVO, LocalDateTime.now()));
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(statusService.findByName(StaticGlossesUtils.ACTIVO)).thenReturn(toBeApproved);
        when(repository.save(any())).thenReturn(entity);

        boolean result = service.toggleStatus(1L, sampleAprovalsDTO().toEntity());
        assertTrue(result);
        verify(loggerService).create(any());
    }

    @Test
    void testToggleStatus_invalidStatus_throwsException() {
        entity.setEstadoId(new StatusEntity(1L, "DESCONOCIDO", LocalDateTime.now()));
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        assertThrows(IllegalStateException.class, () -> service.toggleStatus(1L, sampleAprovalsDTO().toEntity()));
    }

    @Test
    void testAproveAndActivate_idNotFound_throwsException() {
        when(repository.existsById(1L)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> service.aproveAndActivate(1L, new AprobacionesEntity()));
    }

    @Test
    void testAproveAndActivate_withOut_success() {
        AprobacionesEntity aprovals = sampleAprovalsDTO().toEntity();
        aprovals.setComentario("GLOSA_COPIA");

        StaticGlossesEntity copia = getGlossesEntity();
        copia.setName("GLOSA_COPIA");
        copia.setEstadoId(getStatusEntity());

        StaticGlossesEntity original = new StaticGlossesEntity();
        original.setName("GLOSA");
        original.setValue("VALOR_ANTERIOR");

        when(repository.existsById(1L)).thenReturn(true);
        when(statusService.findByName(StaticGlossesUtils.ACTIVO)).thenReturn(toBeApproved);
        when(repository.save(any())).thenReturn(copia);
        when(aprovalsService.findByIdentityWithStatus(anyString(), anyLong())).thenReturn(aprovals);
        when(repository.findById(anyLong())).thenReturn(Optional.of(entity));
        when(statusService.findByName(StaticGlossesUtils.APPROVADO)).thenReturn(toBeApproved);

        Optional<StaticGlossesDTO> result = service.aproveAndActivate(1L, aprovals);
        assertTrue(result.isPresent());
        assertEquals("NAME", result.get().name());
    }

    @Test
    void testAproveAndActivate_withCopy_success() {
        AprobacionesEntity aprovals = sampleAprovalsDTO().toEntity();
        aprovals.setComentario("GLOSA-copia");
        StatusEntity activated = new StatusEntity(
                1L, StaticGlossesUtils.ACTIVO, LocalDateTime.now());

        StatusEntity deactivated =  new StatusEntity(
                2L, StaticGlossesUtils.INACTIVO, LocalDateTime.now());

        when(repository.existsById(anyLong())).thenReturn(true);
        when(statusService.findByName(StaticGlossesUtils.ACTIVO)).thenReturn(activated);
        when(statusService.findByName(StaticGlossesUtils.INACTIVO)).thenReturn(deactivated);
        when(repository.findByNameCopied(anyString(), anyLong()))
                .thenReturn(getGlossesEntity());

        Optional<StaticGlossesDTO> result = service.aproveAndActivate(1L, aprovals);
        assertTrue(result.isPresent());
        assertEquals("NAME", result.get().name());
    }


    void testAproveAndActivate_withCopy_originalNotFound() {
        AprobacionesEntity aprovals = sampleAprovalsDTO().toEntity();
        aprovals.setComentario("GLOSA_COPIA");

        StaticGlossesEntity copia = getGlossesEntity();
        copia.setName("GLOSA_COPIA");

        when(repository.existsById(anyLong())).thenReturn(true);
        when(statusService.findByName(StaticGlossesUtils.ACTIVO)).thenReturn(toBeApproved);
        when(statusService.findByName(StaticGlossesUtils.INACTIVO)).thenReturn(approved);
        when(repository.findByNameCopied("GLOSA_COPIA", 3)).thenReturn(copia);
        when(repository.findByNameCopied("GLOSA", 2)).thenReturn(null); // original no encontrado
        when(aprovalsService.findByIdentityWithStatus(anyString(), anyLong())).thenReturn(aprovals);
        when(repository.findById(anyLong())).thenReturn(Optional.of(entity));
        when(repository.save(any())).thenReturn(copia);

        Optional<StaticGlossesDTO> result = service.aproveAndActivate(anyLong(), aprovals);
        assertTrue(result.isPresent());
        assertEquals("GLOSA", result.get().name());
    }


    void testAproveAndActivate_withCopy_copyNotFound() {
        AprobacionesEntity aprovals = sampleAprovalsDTO().toEntity();
        aprovals.setComentario("GLOSA_COPIA");

        when(repository.existsById(1L)).thenReturn(true);
        when(statusService.findByName(StaticGlossesUtils.ACTIVO)).thenReturn(toBeApproved);
        when(statusService.findByName(StaticGlossesUtils.INACTIVO)).thenReturn(approved);
        when(repository.findByNameCopied("GLOSA_COPIA", 3)).thenReturn(null); // copia no encontrada

        assertThrows(NullPointerException.class, () -> service.aproveAndActivate(1L, aprovals));
    }
    @Test
    void testAproveAndActivate_withoutCopy_delegatesToGlossesAproved() {
        AprobacionesEntity aprovals = sampleAprovalsDTO().toEntity();
        aprovals.setComentario("Comentario sin copia");

        AprobacionesEntity approvalFromDB = sampleAprovalsDTO().toEntity();
        approvalFromDB.setIdentityId(getGlossesEntity().getId());

        when(repository.existsById(anyLong())).thenReturn(true);
        when(aprovalsService.findByIdentityWithStatus(any(), any())).thenReturn(approvalFromDB);
        when(repository.findById(anyLong())).thenReturn(Optional.of(entity));
        when(statusService.findByName(StaticGlossesUtils.ACTIVO)).thenReturn(toBeApproved);
        when(statusService.findByName(StaticGlossesUtils.APPROVADO)).thenReturn(approved);
        when(repository.findById(anyLong())).thenReturn(Optional.of(entity));
        when(repository.save(any())).thenReturn(entity);

        Optional<StaticGlossesDTO> result = service.aproveAndActivate(anyLong(), aprovals);
        assertTrue(result.isPresent());
        verify(loggerService).create(any());
    }

    @Test
    void testGetGlossesByName_success() {
        when(statusService.findByName(StaticGlossesUtils.ACTIVO))
                .thenReturn(getStatusEntity());

        when(repository.findByName(anyString(), anyLong()))
                .thenReturn(List.of(entity));

        Optional<StaticGlossesDTO> result = service.getGlossesByName("NAME");
        assertTrue(result.isPresent());
        assertEquals("NAME", result.get().name());
    }

    @Test
    void testGetGlossesByName_exception() {
        when(statusService.findByName(StaticGlossesUtils.ACTIVO))
                .thenThrow(new RuntimeException("DB error"));

        assertThrows(NoDataFoundException.class, () -> service.getGlossesByName("NAME"));
    }

    @Test
    void testEmptyCheckerOrApproval() {

        Long id = 1L;
        AprobacionesEntity aprobacionesEntity = new AprobacionesEntity();
        aprobacionesEntity.setChecker(""); // Simula campo vacío
        aprobacionesEntity.setComentario(null); // Simula campo nulo

        // Simula que el ID existe
        Mockito.when(repository.existsById(Mockito.eq(id))).thenReturn(true);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.toggleStatus(id, aprobacionesEntity)
        );

        assertEquals("Aprobador y/o comentario no puede ser vacío", exception.getMessage());
    }


    @Test
    void testCreate_userCreatedEmpty_throwsException() {
        StaticGlossesEntity invalidEntity = new StaticGlossesEntity();
        invalidEntity.setUserCreated("");
        assertThrows(IllegalStateException.class, () -> service.create(invalidEntity));
    }

    @Test
    void testUpdate_userUpdatedEmpty_throwsException() {
        when(repository.existsById(1L)).thenReturn(true);
        StaticGlossesEntity invalidEntity = new StaticGlossesEntity();
        invalidEntity.setUserUpdated("");
        assertThrows(IllegalStateException.class, () -> service.update(1L, invalidEntity));
    }

    @Test
    void testCreate_duplicateGloss_throwsException() {
        StaticGlossesEntity entityDuplicate = entity;
        when(statusService.findByName(StaticGlossesUtils.POR_SER_APPROVADO)).thenReturn(toBeApproved);
        when(typeService.findByName(StaticGlossesUtils.IDENTITY_GLOSSES)).thenReturn(getTypeGlosses());
        when(repository.findAll((Specification<StaticGlossesEntity>) any())).thenReturn(List.of(entityDuplicate));
        assertThrows(IllegalStateException.class, () -> service.create(entityDuplicate));
    }

}