package cl.bch.cloud.ms.fglosas.mantenedor.utils;

import cl.bch.cloud.ms.fglosas.mantenedor.entities.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StaticGlossesUtilsTest {
    @NotNull
    private static AprobacionesEntity getAprobacionesEntity() {
        AprobacionesEntity aprovals = new AprobacionesEntity();
        aprovals.setChecker("checker");
        aprovals.setComentario("comentario");
        aprovals.setFechaAprobacion(LocalDateTime.of(2023, 1, 1, 10, 0));
        return aprovals;
    }
    @NotNull
    private static StatusEntity getStatusEntity() {
        return new StatusEntity(
                1L, StaticGlossesUtils.ACTIVO, LocalDateTime.of(2023, 1, 2, 10, 0));
    }

    @NotNull
    private static SignaturesEntity getSignaturesEntity() {
        SignaturesEntity entity = new SignaturesEntity();
        entity.setId(10L);
        entity.setRut("16742032-k");
        entity.setImagen("VBYCRTDR");
        entity.setName("JUAN ALVARES");
        entity.setUserCreated("admin");
        entity.setEstadoId(getStatusEntity());
        return entity;
    }

    @Test
    void testCreateAuthorized() {
        StaticGlossesEntity gloss = new StaticGlossesEntity();
        gloss.setId(10L);

        AprobacionesEntity result = StaticGlossesUtils.createAuthorized(gloss, "checkerUser");

        assertEquals("GLOSAS_ESTATICAS", result.getIdentity());
        assertEquals(10L, result.getIdentityId());
        assertEquals("checkerUser", result.getChecker());
        assertEquals("", result.getComentario());
        assertNotNull(result.getCreateAt());
    }

    protected StaticGlossesEntity getEntity() {
        StaticGlossesEntity gloss = new StaticGlossesEntity();
        gloss.setId(5L);
        gloss.setName("nombre valor");
        gloss.setValue("nuevo valor");
        gloss.setUserCreated("admin");
        return gloss;
    }

    @Test
    void testCreateLogEntity() {
        StaticGlossesEntity gloss = new StaticGlossesEntity();
        gloss.setId(5L);
        gloss.setName("nombre valor");
        gloss.setValue("nuevo valor");
        gloss.setUserCreated("admin");

        LogMantenedorEntity log = StaticGlossesUtils.createLogEntity(gloss, StaticGlossesUtils.UPDATED, "valor anterior");

        assertEquals("GLOSAS_ESTATICAS", log.getIdentity());
        assertEquals(5L, log.getIdentityId());
        assertEquals("ACTUALIZAR", log.getAction());
        assertEquals("nombre valor|nuevo valor", log.getValueNew());
        assertEquals("valor anterior", log.getValueOld());
        assertEquals("admin", log.getUserResposability());
        assertNotNull(log.getCreatedAt());
    }

    @Test
    void testFromParam() {
        AprobacionesEntity update = new AprobacionesEntity();
        AprobacionesEntity source = new AprobacionesEntity();
        source.setChecker("checker");
        source.setComentario("comentario");
        source.setFechaAprobacion(LocalDateTime.now());

        StaticGlossesUtils.fromParam(update, source);

        assertEquals("checker", update.getChecker());
        assertEquals("comentario", update.getComentario());
    }

    @Test
    void testDeleteLogEntity() {
        AprobacionesEntity update = new AprobacionesEntity();
        update.setChecker("systemUser");
        update.setComentario("comentario");

        LogMantenedorEntity log = StaticGlossesUtils.deleteLogEntity(getEntity(), update);

        assertEquals("GLOSAS_ESTATICAS", log.getIdentity());
        assertEquals(5L, log.getIdentityId());
        assertEquals("BORRAR", log.getAction());
        assertEquals("systemUser", log.getUserResposability());
        assertNotNull(log.getCreatedAt());
    }

    @Test
    void testCopyOf() {
        StaticGlossesEntity old = new StaticGlossesEntity();
        old.setUserCreated("UserCreated");
        old.setUserUpdated("UserUpdated");
        old.setCreateAt(LocalDateTime.of(2023, 1, 1, 10, 0));
        old.setUpdateAt(LocalDateTime.of(2023, 1, 2, 10, 0));

        StaticGlossesEntity newi = new StaticGlossesEntity();
        newi.setUserCreated("UserCreated");
        newi.setCreateAt(LocalDateTime.of(2024, 1, 1, 10, 0));

        StaticGlossesUtils.copyOf(newi, old);

        assertEquals("UserCreated", newi.getUserCreated());
        assertEquals("UserCreated", old.getUserUpdated());
        assertEquals(LocalDateTime.of(2023, 1, 1, 10, 0), newi.getCreateAt());
        assertEquals(LocalDateTime.of(2024, 1, 1, 10, 0), old.getUpdateAt());
    }

    @Test
    void testToggleStatusStaticGlosses() {
        StaticGlossesEntity entity = new StaticGlossesEntity();
        entity.setId(10L);
        entity.setName("nombre valor");
        entity.setValue("nuevo valor");
        entity.setUserCreated("admin");
        entity.setUserUpdated("admin");
        entity.setCreateAt(LocalDateTime.of(2023, 1, 1, 10, 0));
        entity.setUpdateAt(LocalDateTime.of(2023, 1, 2, 10, 0));
        entity.setEstadoId(getStatusEntity());

        AprobacionesEntity aprovals = getAprobacionesEntity();

        LogMantenedorEntity result = StaticGlossesUtils.toggleStatus(
                entity, StaticGlossesUtils.ESTADO, aprovals, StaticGlossesUtils.INACTIVO);

        assertEquals(aprovals.getChecker(), result.getUserResposability());
        assertEquals(StaticGlossesUtils.ESTADO, result.getAction());
        assertEquals(StaticGlossesUtils.INACTIVO, result.getValueNew());
    }

    @Test
    void testToggleStatusSignature() {
        SignaturesEntity entity = getSignaturesEntity();

        AprobacionesEntity aprovals = getAprobacionesEntity();

        LogMantenedorEntity result = StaticGlossesUtils.toggleStatus(
                entity, StaticGlossesUtils.ESTADO, aprovals, StaticGlossesUtils.INACTIVO);

        assertEquals(entity.getId(), result.getIdentityId());
        assertEquals(aprovals.getChecker(), result.getUserResposability());
        assertEquals(StaticGlossesUtils.ESTADO, result.getAction());
        assertEquals(StaticGlossesUtils.INACTIVO, result.getValueNew());
    }

    @Test
    void testCreateLogSignatureEntity() {
        SignaturesEntity entity = getSignaturesEntity();
        LogMantenedorEntity result = StaticGlossesUtils.createLogEntity(
                entity, StaticGlossesUtils.CREATED, "valueOld", "admin");

        assertEquals(entity.getId(), result.getIdentityId());
        assertEquals(StaticGlossesUtils.CREATED, result.getAction());
        assertEquals("valueOld", result.getValueOld());
        assertEquals("admin", result.getUserResposability());
    }

    @Test
    void testCreateAprovalEntity() {
        SignaturesEntity entity = getSignaturesEntity();

        AprobacionesEntity result = StaticGlossesUtils.createAprovalEntity(entity);

        assertEquals(entity.getId(), result.getIdentityId());
    }

    @Test
    void quitarSufijoCopia_debeEliminarSufijoCuandoExiste() {
        String resultado = StaticGlossesUtils.quitarSufijoCopia("documento-copia");
        assertEquals("documento", resultado);
    }

    @Test
    void quitarSufijoCopia_noDebeModificarTextoSinSufijo() {
        String resultado = StaticGlossesUtils.quitarSufijoCopia("documento");
        assertEquals("documento", resultado);
    }

    @Test
    void quitarSufijoCopia_debeSerCaseInsensitive() {
        String resultado = StaticGlossesUtils.quitarSufijoCopia("informe-COPIA");
        assertEquals("informe", resultado);
    }

    @Test
    void quitarSufijoCopia_debeRetornarNullSiTextoEsNull() {
        assertNull(StaticGlossesUtils.quitarSufijoCopia(null));
    }

    @Test
    void terminaConCopia_debeDetectarSufijoCorrectamente() {
        assertTrue(StaticGlossesUtils.terminaConCopia("documento-copia"));
        assertTrue(StaticGlossesUtils.terminaConCopia("documento-COPIA"));
    }

    @Test
    void terminaConCopia_debeRetornarFalseSiNoTerminaConCopia() {
        assertFalse(StaticGlossesUtils.terminaConCopia("documento"));
        assertFalse(StaticGlossesUtils.terminaConCopia("copia-documento"));
    }

    @Test
    void terminaConCopia_debeRetornarFalseSiTextoEsNull() {
        assertFalse(StaticGlossesUtils.terminaConCopia(null));
    }

}