package cl.bch.cloud.ms.fglosas.mantenedor.dtos;

import cl.bch.cloud.ms.fglosas.mantenedor.entities.TemplatesEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.io.Serializable;

@RecordBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RqByAttrTmpDTO(
        String mark,
        String transaction,
        RqAttrTmpDTO attr

) implements Serializable {

    public TemplatesEntity toEntity() {
            TemplatesEntity entity = new TemplatesEntity();
            entity.setMarca(mark);
            entity.setTransaccion(transaction);

            try {
                ObjectMapper mapper = new ObjectMapper();
                String attrJson = mapper.writeValueAsString(attr);
                entity.setAttr(attrJson);
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("Error al serializar attr", e);
            }
            return entity;
    }
}