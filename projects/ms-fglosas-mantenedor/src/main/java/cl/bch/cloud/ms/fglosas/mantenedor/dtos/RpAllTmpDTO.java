package cl.bch.cloud.ms.fglosas.mantenedor.dtos;

import cl.bch.cloud.ms.fglosas.mantenedor.entities.TemplatesEntity;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.soabase.recordbuilder.core.RecordBuilder;
import lombok.NonNull;

import java.io.Serializable;
import java.time.LocalDate;

@RecordBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RpAllTmpDTO(
        long id,
        String name,
        String folder,
        long statusId,
        LocalDate createAt,
        LocalDate updateAt,

        String mark
) implements Serializable  {

    public static RpAllTmpDTO fromEntity(
            @NonNull TemplatesEntity entity) {
        return new RpAllTmpDTO(
                entity.getId(),
                entity.getName(),
                entity.getFolder(),
                entity.getEstado().getId(),
                entity.getCreateAt(),
                entity.getUpdateAt(),
                entity.getMarca()
        );
    }
}
