package cl.bch.cloud.ms.fglosas.mantenedor.dtos;

import cl.bch.cloud.ms.fglosas.mantenedor.entities.SignaturesEntity;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.soabase.recordbuilder.core.RecordBuilder;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.io.Serializable;

@RecordBuilder
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record RequestSignatureSingsysDTO(
        @Nullable
        Integer PersonIdType_Id,
        @NonNull
        String PersonIdNumber,
        @Nullable
        String TxId,
        String authToken
) implements Serializable {

        public static RequestSignatureSingsysDTO makeRequestFrom(
                @NonNull SignaturesEntity entity, @NonNull String authToken) {
                return new RequestSignatureSingsysDTO(
                        0, entity.getRut(), "", authToken
                );
        }
}
