package cl.bch.cloud.ms.fglosas.mantenedor.controllers;

import cl.bch.cloud.ms.fglosas.mantenedor.dtos.AprovalsDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.services.AprovalsService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;
import java.util.Optional;

@Tag(name = "Aprobaciones", description = "Tiene como función principal administrar las aprobaciones " +
        "de las glosas dentro del sistema")
@Observed
@RestController
@RequestMapping(path = "/glosses/aprovals/", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class GlossesAprovalsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlossesAprovalsController.class);

    @Autowired
    private final AprovalsService aprovalsService;

    @Operation(summary = "Obtener aprobacion por ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "aprobacion encontrada"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "aprobacion no encontrada",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
            })
    @GetMapping("/{id}")
    public ResponseEntity<AprovalsDTO> getById(@PathVariable long id) {
        LOGGER.info("Aprovals getById - init");
        Optional<AprovalsDTO> response = aprovalsService.getApprovalsById(id);
        LOGGER.info("Aprovals getById - end");
        return response.map(ResponseEntity::ok).orElse(
                ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Devuelve todas las aprobaciones")
    @GetMapping("/")
    public ResponseEntity<List<AprovalsDTO>> getAll() {
        LOGGER.info("getAll - init");
        List<AprovalsDTO> response = aprovalsService.getAllAprovals();
        LOGGER.info("getAll - end");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Crear una nueva aprobacion",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Aprobacion creada exitosamente"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Solicitud incorrecta",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
            })
    @PostMapping("/")
    public ResponseEntity<AprovalsDTO> create(@RequestBody AprovalsDTO entity) {
        LOGGER.info("aprovals create - init");
        try {
            Optional<AprovalsDTO> created = aprovalsService.create(entity.toEntity());
            LOGGER.info("aprovals create - end");
            return created.map(dto ->
                            ResponseEntity.status(HttpStatus.CREATED).body(dto))
                    .orElse(ResponseEntity.badRequest().build());
        } catch (Exception e) {
            LOGGER.error("Error al crear aprobacion", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(entity);
        }
    }

    @Operation(summary = "Actualizar una aprobacion existente")
    @PutMapping("/{id}")
    public ResponseEntity<AprovalsDTO> update(
            @PathVariable Long id,
            @RequestBody AprovalsDTO entity) {
        LOGGER.info("update update - init");
        Optional<AprovalsDTO> response = aprovalsService.update(id, entity.toEntity());
        LOGGER.info("update update - end");
        return response.map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Eliminar una aprobacion")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        LOGGER.info("aprovals delete - init");
        boolean deleted = aprovalsService.delete(id);
        LOGGER.info("aprovals delete - end");
        return deleted ? ResponseEntity.ok().build() :
                ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
