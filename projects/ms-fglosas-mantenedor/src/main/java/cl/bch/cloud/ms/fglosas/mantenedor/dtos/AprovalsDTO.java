package cl.bch.cloud.ms.fglosas.mantenedor.dtos;

import cl.bch.cloud.ms.fglosas.mantenedor.entities.AprobacionesEntity;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

import java.io.Serializable;
import java.time.LocalDateTime;

@RecordBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record AprovalsDTO(
        Long id,
        String identity,
        Long identityId,
        @NotNull
        @NotBlank(message = "El aprobador no puede estar vacío")
        String checker,
        @NotNull
        @NotBlank(message = "El comentario no puede estar vacío")
        String comentario,
        LocalDateTime fechaAprobacion,
        LocalDateTime createAt) implements Serializable, AprovalsDTOBuilder.With {

    public static AprovalsDTO fromEntity(
            @NonNull AprobacionesEntity entity) {
        return new AprovalsDTO(
                entity.getId(),
                entity.getIdentity(),
                entity.getIdentityId(),
                entity.getChecker(),
                entity.getComentario(),
                entity.getFechaAprobacion(),
                entity.getCreateAt()
        );
    }
    public AprobacionesEntity toEntity() {
        return new AprobacionesEntity(
                id, identity, identityId, checker,
                comentario, fechaAprobacion, createAt
        );
    }
}
