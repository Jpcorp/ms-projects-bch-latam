package cl.bch.cloud.ms.fglosas.mantenedor.dtos;

import cl.bch.cloud.ms.fglosas.mantenedor.entities.SignaturesEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.StatusEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.utils.validators.ValidRut;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.soabase.recordbuilder.core.RecordBuilder;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.LocalDateTime;

@RecordBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record SignaturesDTO(
         @Nullable
         Long id,
         @NonNull
         @ValidRut
         String rut,
         @NonNull
         String name,
         @Nullable
         String userCreated,
         @Nullable
         String imagen,
         @Nullable
         long status,
         @Nullable
         LocalDateTime createAt

) implements Serializable {
    public static SignaturesDTO fromEntity(
            @NonNull SignaturesEntity entity) {
        return new SignaturesDTO(
                entity.getId(),
                entity.getRut(),
                entity.getName(),
                entity.getUserCreated(),
                entity.getImagen(),
                entity.getEstadoId().getId(),
                entity.getCreateAt()
        );
    }
    public SignaturesEntity toEntity() {
        StatusEntity statusEntity = new StatusEntity();
        statusEntity.setId(statusEntity.getId());
        return new SignaturesEntity(
                this.id, this.rut, this.name, this.userCreated, this.imagen, statusEntity, this.createAt
        );
    }
}
