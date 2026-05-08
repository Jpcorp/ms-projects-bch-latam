package cl.bch.cloud.ms.fglosas.mantenedor.controllers;

import cl.bch.cloud.ms.fglosas.mantenedor.dtos.RpAllTmpDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.RpIdTmpDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.RqByAttrTmpDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.TmpDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.AprovalsDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.RqUpdateTmpDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.MarcaDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.TransaccionDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.FieldsNotFoundException;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.IlegalActionException;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.InvalidTemplateFieldsException;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.TemplateValidationException;
import cl.bch.cloud.ms.fglosas.mantenedor.services.TemplatesServices;
import io.micrometer.observation.annotation.Observed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Tag(name = "Plantillas", description = "Diseñado para gestionar y mantener la información " +
        "relacionada con plantillas, certificados y estados dentro del sistema")
@Observed
@RestController
@RequestMapping(path = "/template/", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TemplatesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplatesController.class);

    @Autowired
    private final TemplatesServices service;

    @Operation(summary = "Devuelve todas las plantillas")
    @PostMapping("/all")
    public ResponseEntity<List<RpAllTmpDTO>> getAll() {
        LOGGER.info("Template GetAll - init");
        List<RpAllTmpDTO> response = service.getAll();
        LOGGER.info("Template GetAll - end");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtiene una plantilla por identificador y email",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "registro encontrado"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "registro no encontrada",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
            })
    @PostMapping("/{id}")
    public ResponseEntity<RpIdTmpDTO> getById(@PathVariable long id) {
        LOGGER.info("Templates ById - init");
        Optional<RpIdTmpDTO> response = service.getByKey(id);
        LOGGER.info("Templates - end");
        return response.map(ResponseEntity::ok).orElse(
                ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Obtiene una plantilla por marca, nombre transaccion  y atributos como parametros")
    @PostMapping(value = "/get")
    public ResponseEntity<RpIdTmpDTO> getByAttr(@RequestBody RqByAttrTmpDTO request ) {
        LOGGER.info("Templates getByIdAndAttr - init");
        Optional<RpIdTmpDTO> response = service.getByAttr(request.toEntity());
        LOGGER.info("Templates getByIdAndAttr - end");
        return response.map(ResponseEntity::ok).orElse(
                ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Crea un registro de plantilla y sube documento")
    @PostMapping(value = "/")
    public ResponseEntity<Object> createUpload(@RequestPart("data") TmpDTO dto,
                                               @RequestPart("file") MultipartFile file) {
        try {
            LOGGER.info("TemplateCreate - init");
            Optional<TmpDTO> result = service.createUpload(
                    dto.toEntity(), file, dto.mark(), dto.transaction());
            LOGGER.info("TemplateCreate - end");
            return ResponseEntity.status(HttpStatus.CREATED).body(result.get());

        } catch (InvalidTemplateFieldsException e) {
            LOGGER.error("Error en el proceso de carga de plantilla {}", e);
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("error", "Campos inválidos en el template");
            errorBody.put("details", e.getMessage());
            return ResponseEntity.badRequest().body(errorBody);

        } catch (FieldsNotFoundException e) {
            LOGGER.error("Campos del HTML no encontrados en el JSON {}", e);
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("error", "Campos del HTML no encontrados en el JSON");
            errorBody.put("details", e.getMessage());
            return ResponseEntity.badRequest().body(errorBody);

        } catch (TemplateValidationException e) {
            LOGGER.error("Error general en la validación del template {}", e);
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("error", "Error al validar el template");
            errorBody.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);

        } catch (IOException e) {
            LOGGER.error("Error en el proceso de carga de plantilla {}", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al leer el archivo");
        }
    }

    @Operation(summary = "Actualiza un registro de plantilla")
    @PutMapping("/{id}")
    public ResponseEntity<TmpDTO> update(@PathVariable Long id,
                                         @RequestPart("data") RqUpdateTmpDTO dto,
                                         @RequestPart("file") MultipartFile file) {

        try {
            LOGGER.info("template update - init");
            Optional<TmpDTO> response = service.updateUpload(id, dto.toEntity(), file);
            LOGGER.info("template update - end");
            return ResponseEntity.ok().body(response.get());
        } catch (IllegalStateException e) {
            LOGGER.error("Error en el proceso de actualizar firma {}", id, e);
            throw new IlegalActionException(e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.error("Error en el proceso actualizar de plantilla {}", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Aprobar una plantilla")
    @PostMapping("/{id}/approve")
    public ResponseEntity<TmpDTO> approve(
            @PathVariable Long id,
            @RequestBody AprovalsDTO approval) {
        try {
            return service.approve(id, approval.toEntity())
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (Exception e) {
            LOGGER.error("Error al aprobar plantilla", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/marcas")
    public ResponseEntity<List<MarcaDTO>> obtenerMarcas() {
        List<MarcaDTO> marcas = service.getAllMarcas();
        return ResponseEntity.ok(marcas);
    }

    @PostMapping("/transacciones")
    public ResponseEntity<List<TransaccionDTO>> obtenerTransaccionesPorMarca(@RequestBody String idMarca) {
        List<TransaccionDTO> transacciones = service.obtenerTransaccionesPorMarca(idMarca);
        return ResponseEntity.ok(transacciones);
    }

}