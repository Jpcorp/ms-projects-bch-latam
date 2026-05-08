package cl.bch.cloud.ms.fglosas.mantenedor.restclients;

import cl.bch.cloud.dto.motor.plantillas.dtos.CrearPlantillaRq;
import cl.bch.cloud.dto.motor.plantillas.dtos.TemplatesRqDTO;
import cl.bch.cloud.dto.motor.plantillas.dtos.TemplatesRs;
import cl.bch.cloud.ms.fglosas.mantenedor.config.MultipartConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(
        name = "EngineTemplateClient",
        url = "${rest.endpoints.templates.engine.url}",
        configuration = MultipartConfiguration.class
)
public interface EngineTemplateClient {

    @PostMapping(
            value = "/utilidades/documentos",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<TemplatesRs> getTemplate(@RequestBody TemplatesRqDTO reqEngineTemplateDTO);

    @RequestMapping(
            method = RequestMethod.POST,
            value = "utilidades/documentos/plantillas",
            headers = MediaType.MULTIPART_FORM_DATA_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    ResponseEntity<Void> guardarPlantilla(
            @RequestPart("file") MultipartFile file,
            @RequestPart("data") CrearPlantillaRq data
    );
}
