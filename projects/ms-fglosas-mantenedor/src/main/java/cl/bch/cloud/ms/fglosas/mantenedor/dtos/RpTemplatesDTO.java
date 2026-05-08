package cl.bch.cloud.ms.fglosas.mantenedor.dtos;

import cl.bch.cloud.ms.fglosas.mantenedor.entities.StatusEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.TemplatesEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.TmpNotificationEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.soabase.recordbuilder.core.RecordBuilder;
import lombok.NonNull;

import java.io.Serializable;
import java.time.LocalDate;

@RecordBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RpTemplatesDTO(
        long id,
        String name, //form
        long status,
        long notifConf,
        String attr,
        String file,
        @JsonIgnore
        String creador,
        @JsonIgnore
        String actualizador,
        LocalDate createAt,
        LocalDate updateAt

) implements Serializable {
    public static RpTemplatesDTO fromEntity(
            @NonNull TemplatesEntity entity) {
        return new RpTemplatesDTO(
                entity.getId(),
                entity.getName(),
                entity.getEstado().getId(),
                entity.getConfig().getId(),
                entity.getAttr(),
                "",
                entity.getUserCreated(),
                entity.getUserUpdated(),
                entity.getCreateAt(),
                entity.getUpdateAt()
        );
    }
        public TemplatesEntity toEntity() {
            StatusEntity statusEntity = new StatusEntity();
            TmpNotificationEntity config = new TmpNotificationEntity();
            config.setId(notifConf);
            statusEntity.setId(status);
            TemplatesEntity entity = new TemplatesEntity();
            entity.setId(id);
            entity.setName(name);
            entity.setEstado(statusEntity);
            entity.setConfig(config);
            entity.setAttr(attr);
            entity.setUserCreated(creador);
            entity.setUserUpdated(actualizador);
            entity.setCreateAt(createAt);
            entity.setUpdateAt(updateAt);
            return entity;
        }
    }