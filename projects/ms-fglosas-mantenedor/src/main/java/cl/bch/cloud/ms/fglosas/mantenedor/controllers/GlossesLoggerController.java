package cl.bch.cloud.ms.fglosas.mantenedor.controllers;

import cl.bch.cloud.ms.fglosas.mantenedor.dtos.LoggerMaintainerDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.LogMantenedorEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.services.LogMantenedorService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Tag(name = "Registrador de acciones", description = "Tiene como función principal registrar las acciones " +
        "de las glosas dentro del sistema")
@Observed
@RestController
@RequestMapping(path = "/glosses/logs/", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class GlossesLoggerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlossesLoggerController.class);

    @Autowired
    private final LogMantenedorService service;

    @Operation(summary = "Obtener bitacora por ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "bitacora encontrada"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "bitacora no encontrada",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
            })
    @GetMapping("/{id}")
    public ResponseEntity<LoggerMaintainerDTO> getById(@PathVariable long id) {
        LOGGER.info("loggerById - init");
        Optional<LoggerMaintainerDTO> response = service.getLoggerById(id);
        LOGGER.info("loggerById - end");
        return response.map(ResponseEntity::ok).orElse(
                ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Devuelve todas las bitacoras")
    @GetMapping("/")
    public ResponseEntity<List<LoggerMaintainerDTO>> getAll() {
        LOGGER.info("loggerGetAll - init");
        List<LoggerMaintainerDTO> response = service.getAllLoggers();
        LOGGER.info("loggerGetAll - end");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Crear una nueva bitacora",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "bitacoras creada"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Solicitud incorrecta",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
            })
    @PostMapping("/")
    public ResponseEntity<LoggerMaintainerDTO> create(@RequestBody LogMantenedorEntity entity) {
        LOGGER.info("loggerCreate - init");
        try {
            Optional<LoggerMaintainerDTO> created = service.create(LoggerMaintainerDTO.fromEntity(entity));
            LOGGER.info("loggerCreate - end");
            return created.map(dto ->
                            ResponseEntity.status(HttpStatus.CREATED).body(dto))
                    .orElse(ResponseEntity.badRequest().build());
        } catch (Exception e) {
            LOGGER.error("Error al crear logger", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(LoggerMaintainerDTO.fromEntity(entity));
        }
    }


    @Operation(summary = "Eliminar una bitacora")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = service.delete(id);
        return deleted ? ResponseEntity.ok().build() :
                ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "busca bitacora en rango de fechas no mas de 30 dias desde yyyy-mm-dd / hasta yyyy-mm-dd")
    @GetMapping("/{from}/{until}")
    public ResponseEntity<List<LoggerMaintainerDTO>> searchWith(
            @PathVariable String from, @PathVariable String until) {
        try {
            LOGGER.info("searchWith - init");
            LocalDate dateFrom = LocalDate.parse(from);
            LocalDate dateUntil = LocalDate.parse(until);
            Optional<List<LoggerMaintainerDTO>> response = service.searchWithDate(dateFrom, dateUntil);
            LOGGER.info("searchWith - end");
            return response.map(ResponseEntity::ok).orElse(
                    ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (Exception e) {
            LOGGER.error("Error al buscar logger", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
