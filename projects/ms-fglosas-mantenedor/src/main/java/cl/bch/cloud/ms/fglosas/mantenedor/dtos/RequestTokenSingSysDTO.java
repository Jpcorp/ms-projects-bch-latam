package cl.bch.cloud.ms.fglosas.mantenedor.dtos;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.io.Serializable;

@RecordBuilder
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record RequestTokenSingSysDTO(
        String AppId,
        String AppVersion,
        String UserName,
        String Password
) implements Serializable { }
