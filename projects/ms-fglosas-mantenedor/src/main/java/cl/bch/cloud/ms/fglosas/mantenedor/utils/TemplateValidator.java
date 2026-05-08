package cl.bch.cloud.ms.fglosas.mantenedor.utils;

import cl.bch.cloud.ms.fglosas.mantenedor.entities.CampoEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.FieldsNotFoundException;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.InvalidTemplateFieldsException;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.TemplateValidationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TemplateValidator {

    public TemplateValidator(){
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    public void validateTemplateFieldsOrThrow(String htmlContent, List<CampoEntity> templateFields) {
        Set<String> validFieldNames = templateFields.stream()
                .map(field-> field.getNombre())
                .collect(Collectors.toSet());

        Pattern pattern = Pattern.compile("\\$\\{data\\.(\\w+)}");
        Matcher matcher = pattern.matcher(htmlContent);

        List<String> invalidFields = new ArrayList<>();

        while (matcher.find()) {
            String variableName = matcher.group(1);
            if (!validFieldNames.contains(variableName)) {
                invalidFields.add(variableName);
            }
        }

        if (!invalidFields.isEmpty()) {
            throw new InvalidTemplateFieldsException(invalidFields);
        }
    }


    public void validateJsonTemplateFieldsOrThrow(String htmlContent, String json) {
        try {

            Pattern pattern = Pattern.compile("\\$\\{data\\.(\\w+)}");
            Matcher matcher = pattern.matcher(htmlContent);

            List<String> htmlVariables = new ArrayList<>();
            while (matcher.find()) {
                String variableName = matcher.group(1);

                if (!"firma".equals(variableName) && !"nombre_firmante".equals(variableName)) {
                    htmlVariables.add(variableName);
                }
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(json);
            JsonNode dataNode = rootNode.path("data");

            Set<String> jsonKeys = new HashSet<>();
            dataNode.fieldNames().forEachRemaining(jsonKeys::add);

            List<String> fieldsNotFound = htmlVariables.stream()
                    .filter(var -> !jsonKeys.contains(var))
                    .collect(Collectors.toList());

            if (!fieldsNotFound.isEmpty()) {
                throw new FieldsNotFoundException(fieldsNotFound);
            }

        }
        catch (FieldsNotFoundException e) {
            // Re-lanza sin envolver para mantener la información de los campos faltantes
            throw e;
        } catch (Exception e) {
            throw new TemplateValidationException("Error al validar el template contra el JSON", e);
        }
    }



}
