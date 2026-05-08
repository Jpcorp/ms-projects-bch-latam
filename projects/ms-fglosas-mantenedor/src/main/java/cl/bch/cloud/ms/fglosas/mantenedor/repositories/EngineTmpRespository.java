package cl.bch.cloud.ms.fglosas.mantenedor.repositories;

import cl.bch.cloud.dto.motor.plantillas.dtos.ConfiguracionesDto;
import cl.bch.cloud.dto.motor.plantillas.dtos.CrearPlantillaRq;
import cl.bch.cloud.dto.motor.plantillas.dtos.TemplatesRqDTO;
import cl.bch.cloud.dto.motor.plantillas.dtos.TemplatesRs;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.ReqEngineTemplateDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.TemplatesEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.IlegalActionException;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.NoResponseException;
import cl.bch.cloud.ms.fglosas.mantenedor.restclients.EngineTemplateClient;
import cl.bch.cloud.ms.fglosas.mantenedor.utils.StaticGlossesUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RetryableException;
import io.micrometer.observation.annotation.Observed;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Observed
@Repository("EngineTmpRespository")
public class EngineTmpRespository {

    private static final Logger LOGGER = LoggerFactory.getLogger(EngineTmpRespository.class);

    private final EngineTemplateClient client;
    private final ObjectMapper objectMapper;

    @Autowired
    public EngineTmpRespository(EngineTemplateClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }
    public TemplatesRs getTemplateByAttr(TemplatesEntity result) {
        try {
            //armo el request
            ReqEngineTemplateDTO dto  = ReqEngineTemplateDTO.toEntity(result);
            TemplatesRqDTO request = new TemplatesRqDTO();
            request.setTemplate(dto.template());
            ConfiguracionesDto config = objectMapper.readValue(dto.atributos(), ConfiguracionesDto.class);
            request.setAtributos(config);
            Map<String, Object> data = objectMapper.readValue(dto.data(), Map.class);
            request.setData(data);
            LOGGER.info("Request enviado al motor: {}", objectMapper.writeValueAsString(request));
            ResponseEntity<TemplatesRs> engine = client.getTemplate(request);
            if (engine.getStatusCode() == HttpStatus.OK) {
                return engine.getBody();
            } else {
                ResponseEntity<TemplatesRs> response =
                        new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                return response.getBody();
            }

        } catch (RetryableException e) {
            LOGGER.error("Error al obtener documento", e);
            throw new NoResponseException("Service engine template not response", e);
        } catch (JsonMappingException e) {
            throw new IlegalActionException("Error al establecer mapping", e);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error al parsear atributos", e);
            throw new IlegalActionException("Error al procesar atributos", e);
        } catch (Exception e) {
            LOGGER.error("Error al obtener documento", e);
            throw new IlegalActionException("Error al obtener plantilla", e);
        }
    }

    public void createEnginetmp(TemplatesEntity register, MultipartFile file) {
        try {
            // Elimina la barra del path y construye el DTO
            String filename = StringUtils.remove(register.getName(), "/");
            CrearPlantillaRq request = new CrearPlantillaRq(filename, register.getFolder());

            // Llama al cliente Feign usando el JSON como String
            ResponseEntity<Void> response = client.guardarPlantilla(file, request);
            // Verifica la respuesta
            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                register.setEstadoDocto(StaticGlossesUtils.CARGADO);
            }

        } catch (Exception e) {
            LOGGER.error("Error al subir el documento al motor", e);
            throw new NoResponseException("Error al subir el documento al motor de plantillas", e);
        }
    }
}
