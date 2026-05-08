package cl.bch.cloud.ms.fglosas.mantenedor.dtos;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.io.Serializable;

@RecordBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RespEngineTemplateDTO (
        String file,
        String folder,
        String fileName
) implements Serializable {

}
