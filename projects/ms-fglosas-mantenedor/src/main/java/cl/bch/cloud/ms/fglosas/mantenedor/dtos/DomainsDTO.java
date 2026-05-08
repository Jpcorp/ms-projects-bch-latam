package cl.bch.cloud.ms.fglosas.mantenedor.dtos;

import cl.bch.cloud.ms.fglosas.mantenedor.entities.TmpDomainsEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NonNull;

import java.io.Serializable;
import java.time.LocalDate;

public record DomainsDTO(
        long id,
        String name,
        String folder,
        @JsonIgnore
        LocalDate createAt
) implements Serializable {

    public static DomainsDTO fromEntity(@NonNull TmpDomainsEntity entity) {
        return new DomainsDTO(
                entity.getId(),
                entity.getName(),
                entity.getFolder(),
                entity.getCreateAt()
        );
    }
}
