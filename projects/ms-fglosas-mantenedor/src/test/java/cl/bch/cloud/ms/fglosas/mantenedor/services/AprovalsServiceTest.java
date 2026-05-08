package cl.bch.cloud.ms.fglosas.mantenedor.services;

import cl.bch.cloud.ms.fglosas.mantenedor.dtos.AprovalsDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.LoggerMaintainerDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.AprobacionesEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.NoDataFoundException;
import cl.bch.cloud.ms.fglosas.mantenedor.repositories.AprobacionesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AprovalsServiceTest {

    private AprobacionesRepository repository;
    private AprovalsService service;

    @BeforeEach
    void setUp() {
        repository = mock(AprobacionesRepository.class);
        service = new AprovalsService(repository);
    }

    @Test
    void testGetApprovalsById_found() {
        AprobacionesEntity entity = new AprobacionesEntity();
        entity.setId(1L);
        entity.setIdentity("GLOSAS_ESTATICAS");

        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        Optional<AprovalsDTO> result = service.getApprovalsById(1L);

        assertTrue(result.isPresent());
        assertEquals("GLOSAS_ESTATICAS", result.get().identity());
    }

    @Test
    void testGetApprovalsById_notFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Optional<AprovalsDTO> result = service.getApprovalsById(99L);

        assertTrue(result.isPresent());
        assertEquals("", result.get().identity());
    }

    @Test
    void testGetApprovalsById_exception() {
        when(repository.findById(1L)).thenThrow(new RuntimeException("DB error"));
        Optional<AprovalsDTO> result = service.getApprovalsById(1L);

        assertTrue(result.isPresent());
        AprovalsDTO dto = result.get();

        assertNull(dto.id());
        assertEquals("", dto.identity());
        assertNull(dto.identityId());
    }

    @Test
    void testGetAllAprovals() {
        AprobacionesEntity entity = new AprobacionesEntity();
        entity.setId(1L);
        entity.setIdentity("GLOSAS_ESTATICAS");

        when(repository.findAll()).thenReturn(List.of(entity));

        List<AprovalsDTO> result = service.getAllAprovals();

        assertEquals(1, result.size());
        assertEquals("GLOSAS_ESTATICAS", result.get(0).identity());
    }

    @Test
    void testCreate() {
        AprobacionesEntity entity = new AprobacionesEntity();
        entity.setId(1L);
        entity.setIdentity("GLOSAS_ESTATICAS");

        when(repository.save(any())).thenReturn(entity);

        Optional<AprovalsDTO> result = service.create(entity);

        assertTrue(result.isPresent());
        assertEquals("GLOSAS_ESTATICAS", result.get().identity());
    }

    @Test
    void testUpdate() {
        AprobacionesEntity entity = new AprobacionesEntity();
        entity.setId(1L);
        entity.setChecker("JALVARES");
        entity.setComentario("OK");
        entity.setFechaAprobacion(LocalDateTime.now());
        entity.setIdentity("GLOSAS_ESTATICAS");

        when(repository.findById(anyLong())).thenReturn(Optional.of(entity));

        when(repository.save(any())).thenReturn(entity);

        Optional<AprovalsDTO> result = service.update(1L, entity);

        assertTrue(result.isPresent());
        assertEquals("GLOSAS_ESTATICAS", result.get().identity());
    }

    @Test
    void testUpdate_empty() {
        AprobacionesEntity entity = new AprobacionesEntity();
        entity.setId(1L);
        entity.setIdentity("GLOSAS_ESTATICAS");

        when(repository.existsById(anyLong())).thenReturn(false);

        Optional<AprovalsDTO> result = service.update(1L, entity);

        assertEquals(Optional.empty(), result);
    }

    @Test
    void testDelete_success() {
        when(repository.existsById(1L)).thenReturn(true);

        boolean result = service.delete(1L);

        assertTrue(result);
        verify(repository).deleteById(1L);
    }

    @Test
    void testDelete_notFound() {
        when(repository.existsById(99L)).thenReturn(false);

        boolean result = service.delete(99L);

        assertFalse(result);
        verify(repository, never()).deleteById(99L);
    }

    @Test
    void testFindByIdentityWithStatus() {
        AprobacionesEntity entity = new AprobacionesEntity();
        entity.setId(1L);
        entity.setIdentity("GLOSAS_ESTATICAS");

        when(repository.findIdentityByIdStatus("GLOSAS_ESTATICAS", 1L)).thenReturn(entity);

        AprobacionesEntity result = service.findByIdentityWithStatus("GLOSAS_ESTATICAS", 1L);

        assertNotNull(result);
        assertEquals("GLOSAS_ESTATICAS", result.getIdentity());
    }
}
