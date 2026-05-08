package cl.bch.cloud.ms.fglosas.mantenedor.services;

import cl.bch.cloud.ms.fglosas.mantenedor.entities.StatusEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.repositories.StatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatusServiceTest {

    private StatusRepository repository;
    private StatusService service;

    @BeforeEach
    void setUp() {
        repository = mock(StatusRepository.class);
        service = new StatusService(repository);
    }

    @Test
    void testGetStatus() {
        StatusEntity expected = new StatusEntity(1L, "ACTIVO", LocalDateTime.now());
        when(repository.findByNameStatus("ACTIVO")).thenReturn(expected);

        StatusEntity result = service.getStatus( "ACTIVO");

        assertNotNull(result);
        assertEquals("ACTIVO", result.getName());
        verify(repository, times(1)).findByNameStatus("ACTIVO");
    }

    @Test
    void testFindByName() {
        StatusEntity expected = new StatusEntity(2L, "INACTIVO", LocalDateTime.now());
        when(repository.findByName("INACTIVO")).thenReturn(expected);

        StatusEntity result = service.findByName("INACTIVO");

        assertNotNull(result);
        assertEquals("INACTIVO", result.getName());
        verify(repository, times(1)).findByName("INACTIVO");
    }

}