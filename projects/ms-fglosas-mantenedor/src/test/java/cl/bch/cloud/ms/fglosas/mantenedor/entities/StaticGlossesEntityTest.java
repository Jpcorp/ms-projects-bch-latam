package cl.bch.cloud.ms.fglosas.mantenedor.entities;

import cl.bch.cloud.ms.fglosas.mantenedor.utils.StaticGlossesUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StaticGlossesEntityTest {

    static Long id;
    static String name;
    static String value;
    static String userCreated; //campo
    static String userUpdated;
    static StatusEntity estadoId;
    static LocalDateTime createAt;
    static LocalDateTime updateAt;
    StaticGlossesEntity entity;
    TypeGlossesEntity typeGlossesEntity;

    @BeforeEach
    void setUp() {
        id = 3L;
        name = "Pap centinela tarjeta de crédito";
        value = "Cargo aplicado por el saldo adeudado y vencido en la tarjeta de crédito. El proceso centinela " +
                "aplica el “Pago Automático de Producto” desde el día siguiente del vencimiento, buscando saldos " +
                "disponibles en las cuentas, para cubrir el monto adeudado. Se sugiere revisar la facturación de la " +
                "tarjeta de crédito, donde se puede visualizar el monto mínimo cobrado.";
        userCreated = "JLPERALES";
        userUpdated = "JOSEJOSE";
        estadoId = getStatusEntity();
        typeGlossesEntity = getTypeGlossesEntity();
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();

        entity = new StaticGlossesEntity(id, name, value, userCreated, userUpdated,
                estadoId, typeGlossesEntity, createAt, updateAt);
    }

    private TypeGlossesEntity getTypeGlossesEntity() {
        return new TypeGlossesEntity(1L, StaticGlossesUtils.IDENTITY_GLOSSES, LocalDateTime.now());
    }

    private StatusEntity getStatusEntity() {
        return new StatusEntity(1L, StaticGlossesUtils.POR_SER_APPROVADO,
                LocalDateTime.now());
    }

    @Test
    void testGetters() {
        assertEquals(id, entity.getId());
        assertEquals(name, entity.getName());
        assertEquals(value, entity.getValue());
        assertEquals(userCreated, entity.getUserCreated());
        assertEquals(userUpdated, entity.getUserUpdated());
        assertEquals(estadoId, entity.getEstadoId());
        assertEquals(createAt, entity.getCreateAt());
        assertEquals(updateAt, entity.getUpdateAt());
    }

    @Test
    void testSetters() {
        StaticGlossesEntity newEntity = new StaticGlossesEntity();
        newEntity.setId(10L);
        newEntity.setName("Nueva Glosa");
        newEntity.setValue("Nuevo valor");
        newEntity.setUserCreated("CREADOR");
        newEntity.setUserUpdated("ACTUALIZADOR");
        newEntity.setEstadoId(estadoId);
        newEntity.setCreateAt(createAt);
        newEntity.setUpdateAt(updateAt);

        assertEquals(10L, newEntity.getId());
        assertEquals("Nueva Glosa", newEntity.getName());
        assertEquals("Nuevo valor", newEntity.getValue());
        assertEquals("CREADOR", newEntity.getUserCreated());
        assertEquals("ACTUALIZADOR", newEntity.getUserUpdated());
        assertEquals(estadoId, newEntity.getEstadoId());
        assertEquals(createAt, newEntity.getCreateAt());
        assertEquals(updateAt, newEntity.getUpdateAt());
    }
}