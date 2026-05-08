package cl.bch.cloud.ms.fglosas.mantenedor.dtos;

import cl.bch.cloud.ms.fglosas.mantenedor.entities.LogMantenedorEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.soabase.recordbuilder.core.RecordBuilder;
import lombok.NonNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RecordBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record LoggerMaintainerDTO(
        Long id,
        String identity,
        Long identityId,
        String action,
        String valueOld,
        String valueNew,
        String userResposability,
        @JsonIgnore
        LocalDateTime createdAt
) implements Serializable {

    public static LoggerMaintainerDTO fromEntity(
            @NonNull LogMantenedorEntity entity) {
        return new LoggerMaintainerDTO(
                entity.getId(),
                entity.getIdentity(),
                entity.getIdentityId(),
                entity.getAction(),
                entity.getValueOld(),
                entity.getValueNew(),
                entity.getUserResposability(),
                entity.getCreatedAt()
        );
    }

    public LogMantenedorEntity toEntity() {
        return new LogMantenedorEntity(
                id, identity, identityId, action, valueOld,
                valueNew, userResposability, createdAt
        );
    }

    @JsonProperty("created_at")
    public String getFormattedCreatedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return createdAt != null ? createdAt.format(formatter) : null;
    }

}
