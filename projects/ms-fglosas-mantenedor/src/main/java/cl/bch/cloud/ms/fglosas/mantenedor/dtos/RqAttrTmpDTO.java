package cl.bch.cloud.ms.fglosas.mantenedor.dtos;

import cl.bch.cloud.dto.motor.plantillas.dtos.ConfiguracionesDto;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.io.Serializable;
import java.util.Map;

@RecordBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RqAttrTmpDTO(

        ConfiguracionesDto atributos,
        Map<String, Object> data

) implements Serializable {


}