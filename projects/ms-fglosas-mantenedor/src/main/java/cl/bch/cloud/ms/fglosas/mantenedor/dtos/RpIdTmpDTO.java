package cl.bch.cloud.ms.fglosas.mantenedor.dtos;

import cl.bch.cloud.ms.fglosas.mantenedor.entities.TemplatesEntity;
import lombok.NonNull;

import java.io.Serializable;
import java.time.LocalDate;

public record RpIdTmpDTO(
    long id,
    String name,
    long estado,
    String file,
    LocalDate createAt,
    LocalDate updateAt
) implements Serializable {
    public static RpIdTmpDTO fromEntity(
            @NonNull TemplatesEntity entity, String file) {
        return new RpIdTmpDTO(
                entity.getId(),
                entity.getName(),
                entity.getEstado().getId(),
                file,
                entity.getCreateAt(),
                entity.getUpdateAt()
        );
    }
}
