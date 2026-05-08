package cl.bch.cloud.ms.fglosas.mantenedor.dtos;

import cl.bch.cloud.ms.fglosas.mantenedor.entities.StatusEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.TemplatesEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.TmpNotificationEntity;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.io.Serializable;

@RecordBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RqUpdateTmpDTO (
        long status,
        long notification,
        String attr,
        String userUpdated

) implements Serializable {
    public TemplatesEntity toEntity() {
        StatusEntity statusEntity = new StatusEntity();
        statusEntity.setId(status);

        TmpNotificationEntity notificacion = new TmpNotificationEntity();
        notificacion.setId(notification);

        TemplatesEntity entity = new TemplatesEntity();
        entity.setEstado(statusEntity);
        entity.setConfig(notificacion);
        entity.setAttr(attr);
        entity.setUserUpdated(userUpdated);
        return entity;
    }
}
