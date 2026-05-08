package cl.bch.cloud.ms.fglosas.mantenedor.dtos;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.soabase.recordbuilder.core.RecordBuilder;
import lombok.NonNull;

import java.io.Serializable;

@RecordBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record MessageDTO(
        @NonNull
        String status,
        @NonNull
        String message) implements Serializable {

        public static MessageDTO toParams(String status, String message) {
                return new MessageDTO( status, message);
        }
}