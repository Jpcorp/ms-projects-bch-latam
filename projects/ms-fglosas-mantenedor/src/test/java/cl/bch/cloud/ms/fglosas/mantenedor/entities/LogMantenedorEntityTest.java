package cl.bch.cloud.ms.fglosas.mantenedor.entities;

import cl.bch.cloud.ms.fglosas.mantenedor.utils.StaticGlossesUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class LogMantenedorEntityTest {

    static Long id;
    static String identity;
    static Long identityId;
    static String action;
    static String valueOld;
    static String valueNew;
    static String userResposability;
    static LocalDateTime createdAt;

    LogMantenedorEntity entity;

    @BeforeEach
    void setUp() {
        id = 1L;
        identity = StaticGlossesUtils.IDENTITY_GLOSSES;
        identityId = 3L;
        action = StaticGlossesUtils.CREATED;
        valueOld = "Cargo aplicado por el saldo adeudado y vencido en la tarjeta de crédito. El proceso centinela " +
                "aplica el “Pago Automático de Producto” desde el día siguiente del vencimiento, buscando saldos " +
                "disponibles en las cuentas, para cubrir el monto adeudado. Se sugiere revisar la facturación de la " +
                "tarjeta de crédito, donde se puede visualizar el monto mínimo cobrado.";
        valueNew = "Cargo aplicado por el saldo adeudado";
        userResposability = "JLPERALES";
        createdAt = LocalDateTime.now();

        entity = new LogMantenedorEntity(id, identity, identityId, action, valueOld,
                valueNew, userResposability, createdAt);
    }

    @Test
    void testGetters() {
        assertEquals(id, entity.getId());
        assertEquals(identity, entity.getIdentity());
        assertEquals(identityId, entity.getIdentityId());
        assertEquals(action, entity.getAction());
        assertEquals(valueOld, entity.getValueOld());
        assertEquals(valueNew, entity.getValueNew());
        assertEquals(userResposability, entity.getUserResposability());
        assertEquals(createdAt, entity.getCreatedAt());
    }

    @Test
    void testSetters() {
        LogMantenedorEntity newEntity = new LogMantenedorEntity();
        newEntity.setId(2L);
        newEntity.setIdentity("NEW_IDENTITY");
        newEntity.setIdentityId(5L);
        newEntity.setAction("UPDATED");
        newEntity.setValueOld("Old value");
        newEntity.setValueNew("New value");
        newEntity.setUserResposability("NEW_USER");
        newEntity.setCreatedAt(createdAt);

        assertEquals(2L, newEntity.getId());
        assertEquals("NEW_IDENTITY", newEntity.getIdentity());
        assertEquals(5L, newEntity.getIdentityId());
        assertEquals("UPDATED", newEntity.getAction());
        assertEquals("Old value", newEntity.getValueOld());
        assertEquals("New value", newEntity.getValueNew());
        assertEquals("NEW_USER", newEntity.getUserResposability());
        assertEquals(createdAt, newEntity.getCreatedAt());
    }
}