package cl.bch.cloud.ms.fglosas.mantenedor.dtos;

import cl.bch.cloud.ms.fglosas.mantenedor.entities.TemplatesEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.soabase.recordbuilder.core.RecordBuilder;
import lombok.NonNull;

import java.io.Serializable;

@RecordBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ReqEngineTemplateDTO (
     String template,
     String atributos,
     String data

) implements Serializable {

    public static ReqEngineTemplateDTO toDto(RpTemplatesDTO tmp) {
        JsonNode atributosNode = getJsonNode(tmp.attr(), "atributos");
        JsonNode dataNode = getJsonNode(tmp.attr(), "data");

        String atributos = atributosNode != null ? atributosNode.toPrettyString() : "{}";
        String data = dataNode != null ? dataNode.toPrettyString() : "{}";
        String template = tmp.file().concat(tmp.name());

        return new ReqEngineTemplateDTO(template, atributos, data);
    }

    public static JsonNode getJsonNode(@NonNull String raw, @NonNull String nodeName) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(raw);
            return findNodeRecursively(root, nodeName);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "No se pudo procesar al obtener plantilla: nodo '" + nodeName + "' en '" + raw + "'", e
            );
        }
    }
    
    

    private static JsonNode findNodeRecursively(JsonNode current, String nodeName) {
        if (current.has(nodeName)) {
            return current.get(nodeName);
        }
        for (JsonNode child : current) {
            JsonNode result = findNodeRecursively(child, nodeName);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public static ReqEngineTemplateDTO toEntity(TemplatesEntity result) {
        JsonNode atributosNode = getJsonNode(result.getAttr(), "atributos");
        JsonNode dataNode = getJsonNode(result.getAttr(), "data");

        String atributos = atributosNode != null ? atributosNode.toPrettyString() : "{}";
        String data = dataNode != null ? dataNode.toPrettyString() : "{}";
        String template = result.getFolder().concat(result.getName());

        return new ReqEngineTemplateDTO(template, atributos, data);
    }

    public static String addNodeSignature(String data, String signature, String value) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(data);
        if (root.isObject()) {
            ObjectNode objectNode = (ObjectNode) root;
            objectNode.put(signature, value);
            return mapper.writeValueAsString(objectNode);
        } else {
            throw new IllegalArgumentException("El JSON raíz no es un objeto.");
        }
    }

}
