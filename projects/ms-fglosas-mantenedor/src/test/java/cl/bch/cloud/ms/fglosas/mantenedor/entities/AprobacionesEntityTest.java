package cl.bch.cloud.ms.fglosas.mantenedor.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AprobacionesEntityTest {

    static Long id;
    static String identity;
    static Long identityId;
    static String checker;
    static String comentario;
    static LocalDateTime fechaAprobacion;
    static LocalDateTime createAt;

    AprobacionesEntity entity;

    @BeforeEach
    void setUp() {
        id = 1L;
        identity = "identity";
        identityId = 1L;
        checker = "checker";
        comentario = "comentario";
        fechaAprobacion = LocalDateTime.now();
        createAt = LocalDateTime.now();

        entity = new AprobacionesEntity(id, identity, identityId, checker,
                comentario, fechaAprobacion, createAt);
    }

    @Test
    void testGetters() {
        assertEquals(id, entity.getId());
        assertEquals(identity, entity.getIdentity());
        assertEquals(identityId, entity.getIdentityId());
        assertEquals(checker, entity.getChecker());
        assertEquals(comentario, entity.getComentario());
        assertEquals(fechaAprobacion, entity.getFechaAprobacion());
        assertEquals(createAt, entity.getCreateAt());
    }

    @Test
    void testSetters() {
        AprobacionesEntity newEntity = new AprobacionesEntity();
        newEntity.setId(2L);
        newEntity.setIdentity("new_identity");
        newEntity.setIdentityId(2L);
        newEntity.setChecker("new_checker");
        newEntity.setComentario("nuevo comentario");
        newEntity.setFechaAprobacion(fechaAprobacion);
        newEntity.setCreateAt(createAt);

        assertEquals(2L, newEntity.getId());
        assertEquals("new_identity", newEntity.getIdentity());
        assertEquals(2L, newEntity.getIdentityId());
        assertEquals("new_checker", newEntity.getChecker());
        assertEquals("nuevo comentario", newEntity.getComentario());
        assertEquals(fechaAprobacion, newEntity.getFechaAprobacion());
        assertEquals(createAt, newEntity.getCreateAt());
    }
}