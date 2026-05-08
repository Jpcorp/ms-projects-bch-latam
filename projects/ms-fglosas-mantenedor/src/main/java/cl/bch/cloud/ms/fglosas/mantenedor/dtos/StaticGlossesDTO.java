package cl.bch.cloud.ms.fglosas.mantenedor.dtos;

import cl.bch.cloud.ms.fglosas.mantenedor.entities.StaticGlossesEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.StatusEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.TypeGlossesEntity;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.LocalDateTime;

@RecordBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record StaticGlossesDTO(
         @Nullable
         Long id,
         @NotBlank(message = "El nombre no puede estar vacío")
         String name,
         @NonNull
         @NotBlank(message = "El valor no puede estar vacío")
         String value,
         String userCreated, //campo
         @Nullable
         String userUpdated,
         long status,
         long tipoGlosa,
         @Nullable
         LocalDateTime createAt,
         @Nullable
         LocalDateTime updateAt
) implements Serializable, StaticGlossesDTOBuilder.With {
    public static StaticGlossesDTO fromEntity(
            @NonNull StaticGlossesEntity entity) {
        return new StaticGlossesDTO(
                entity.getId(),
                entity.getName(),
                entity.getValue(),
                entity.getUserCreated(),
                entity.getUserUpdated(),
                entity.getEstadoId().getId(),
                entity.getTipoGlosa().getId(),
                entity.getCreateAt(),
                entity.getUpdateAt()
        );
    }
    public StaticGlossesEntity toEntity() {
        StatusEntity statusEntity = new StatusEntity();
        TypeGlossesEntity typeGlossesEntity = new TypeGlossesEntity();
        statusEntity.setId(this.status);
        return new StaticGlossesEntity(
                this.id, this.name, this.value, this.userCreated, this.userUpdated,
                statusEntity, typeGlossesEntity, this.createAt, this.updateAt
        );
    }
}
