package cl.bch.cloud.ms.fglosas.mantenedor.dtos;

import cl.bch.cloud.ms.fglosas.mantenedor.entities.StatusEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.TemplatesEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.TmpNotificationEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.utils.StaticGlossesUtils;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.io.Serializable;

@RecordBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TmpDTO(
        @Nullable
        long id,
        @NotNull
        String name, //form
        long status,
        @NotNull
        long notificationId, //form
        String attr,
        @NotNull
        String userCreated,
        String userUpdated,
        @NotNull
        String mark,
        @NotNull
        String transaction

) implements Serializable {

    public static TmpDTO fromEntity(@NonNull TemplatesEntity entity, String attr) {
        return new TmpDTO( entity.getId(), entity.getName(),
                entity.getEstado().getId(), entity.getConfig().getId(),
                attr, entity.getUserCreated(), entity.getUserUpdated(),
                entity.getMarca(), entity.getTransaccion()
        );
    }

    public TemplatesEntity toEntity() {
            StatusEntity statusEntity = new StatusEntity();
            statusEntity.setId(status);

            TmpNotificationEntity notificacion = new TmpNotificationEntity();
            notificacion.setId(notificationId);
            String nombre = StaticGlossesUtils.versionarNombreSiEsNecesario(name, mark);

            TemplatesEntity entity = new TemplatesEntity();
            entity.setMarca(mark);
            entity.setId(id);
            entity.setName(nombre);
            entity.setEstado(statusEntity);
            entity.setConfig(notificacion);
            entity.setAttr(attr);
            entity.setUserCreated(userCreated);
            entity.setUserUpdated(userUpdated);
            entity.setTransaccion(transaction);
            return entity;
        }
}