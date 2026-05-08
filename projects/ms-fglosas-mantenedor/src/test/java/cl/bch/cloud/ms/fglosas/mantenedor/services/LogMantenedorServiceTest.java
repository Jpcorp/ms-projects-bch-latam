package cl.bch.cloud.ms.fglosas.mantenedor.services;

import cl.bch.cloud.ms.fglosas.mantenedor.dtos.LoggerMaintainerDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.LogMantenedorEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.NoDataFoundException;
import cl.bch.cloud.ms.fglosas.mantenedor.repositories.LogMantenedorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LogMantenedorServiceTest {

    private LogMantenedorRepository repository;
    private LogMantenedorService service;

    @BeforeEach
    void setUp() {
        repository = mock(LogMantenedorRepository.class);
        service = new LogMantenedorService(repository);
    }

    private LogMantenedorEntity getEntity () {
        LogMantenedorEntity entity = new LogMantenedorEntity();
        entity.setId(1L);
        entity.setIdentityId(23L);
        entity.setAction("CREATE");
        entity.setValueOld("WQWQ");
        entity.setValueNew("WQWQQW");
        entity.setUserResposability("AGUERRERO");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setIdentity("GLOSAS_ESTATICAS");
        return entity;
    }

    @Test
    void testGetLoggerById_found() {
        LogMantenedorEntity entity = new LogMantenedorEntity();
        entity.setId(1L);
        entity.setIdentity("GLOSAS_ESTATICAS");

        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        Optional<LoggerMaintainerDTO> result = service.getLoggerById(1L);

        assertTrue(result.isPresent());
        assertEquals("GLOSAS_ESTATICAS", result.get().identity());
    }

    @Test
    void testGetLoggerById_notFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Optional<LoggerMaintainerDTO> result = service.getLoggerById(99L);

        assertTrue(result.isPresent());
        assertEquals("", result.get().identity());
    }

    @Test
    void testGetAllLoggers() {
        LogMantenedorEntity entity = new LogMantenedorEntity();
        entity.setId(1L);
        entity.setIdentity("GLOSAS_ESTATICAS");

        when(repository.findAll()).thenReturn(List.of(entity));

        List<LoggerMaintainerDTO> result = service.getAllLoggers();

        assertEquals(1, result.size());
        assertEquals("GLOSAS_ESTATICAS", result.get(0).identity());
    }

    @Test
    void testCreate() {
        LogMantenedorEntity entity = getEntity();

        when(repository.save(any())).thenReturn(entity);

        Optional<LoggerMaintainerDTO> result = service.create(
                LoggerMaintainerDTO.fromEntity(entity));

        assertTrue(result.isPresent());
        assertEquals("GLOSAS_ESTATICAS", result.get().identity());
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
    void testFromDateBeforeThirtyDaysAgo() {
        LocalDate today = LocalDate.now();
        LocalDate from = today.minusDays(31);
        LocalDate until = today;

        Optional<List<LoggerMaintainerDTO>> result = service.searchWithDate(from, until);
        assertTrue(result.isEmpty(), "Debe retornar Optional.empty() si 'from' es muy antiguo");
    }

    @Test
    void testUntilDateAfterToday() {
        LocalDate today = LocalDate.now();
        LocalDate from = today.minusDays(10);
        LocalDate until = today.plusDays(1);

        Optional<List<LoggerMaintainerDTO>> result = service.searchWithDate(from, until);
        assertTrue(result.isEmpty(), "Debe retornar Optional.empty() si 'until' es en el futuro");
    }

    @Test
    void testFromAfterUntil() {
        LocalDate today = LocalDate.now();
        LocalDate from = today;
        LocalDate until = today.minusDays(1);

        Optional<List<LoggerMaintainerDTO>> result = service.searchWithDate(from, until);
        assertTrue(result.isEmpty(), "Debe retornar Optional.empty() si 'from' es después de 'until'");
    }
    @Test
    void testValidDateRangeReturnsFilteredResults() {
        LocalDate today = LocalDate.now();
        LocalDate from = today.minusDays(5);
        LocalDate until = today;

        when(repository.findAll()).thenReturn(List.of(getEntity()));

        Optional<List<LoggerMaintainerDTO>> result = service.searchWithDate(from, until);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size(), "Debe retornar solo los registros dentro del rango de fechas");
    }

}
