package cl.bch.cloud.ms.fglosas.mantenedor.controllers;

import cl.bch.cloud.ms.fglosas.mantenedor.dtos.AprovalsDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.MessageDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.StaticGlossesDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.IlegalActionException;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.NoDataFoundException;
import cl.bch.cloud.ms.fglosas.mantenedor.services.StaticGlossesService;
import io.micrometer.observation.annotation.Observed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Tag(name = "Glosas Estaticas", description = "Microservicio tiene como función principal gestionar y actualizar " +
        "el estado de las glosas dentro del sistema")
@Observed
@RestController
@RequestMapping(path = "/glosses/static/", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class GlossesMantController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlossesMantController.class);

    @Autowired
    private final StaticGlossesService staticGlossesService;

    @Operation(summary = "Obtener glosa por ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Glosa encontrada"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Glosa no encontrada",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
            })
    @PostMapping("/{id}")
    @ExceptionHandler(NoDataFoundException.class)
    public ResponseEntity<StaticGlossesDTO> getById(@PathVariable long id) {
        LOGGER.info("getById - init");
        Optional<StaticGlossesDTO> response = staticGlossesService.getGlossesById(id);
        LOGGER.info("getById - end");
        return response.map(ResponseEntity::ok).orElse(
                ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Devuelve todas las glosas")
    @PostMapping("/all")
    public ResponseEntity<List<StaticGlossesDTO>> getAll() {
        LOGGER.info("getAll - init");

        List<StaticGlossesDTO> response = new ArrayList<>(staticGlossesService.getAllGlosses());
        response.sort(Comparator.comparing(StaticGlossesDTO::createAt,
                Comparator.nullsLast(Comparator.reverseOrder())));

        LOGGER.info("getAll - end");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Crear una nueva glosa",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Glosa creada exitosamente"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Solicitud incorrecta",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
            })
    @PostMapping("/")
    public ResponseEntity<Object> createGlosa(@Valid @RequestBody StaticGlossesDTO staticGlossesDTO) {
        try {
            LOGGER.info("create - init");
            Optional<StaticGlossesDTO> created = staticGlossesService
                    .create(staticGlossesDTO.toEntity());
            LOGGER.info("create - end");
            return ResponseEntity.status(HttpStatus.CREATED).body(created.get());
        } catch (IllegalStateException e) {
            LOGGER.error("Error al crear glosa", e);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            LOGGER.error("Error al crear glosa", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @Operation(summary = "Actualizar una glosa existente")
    @PutMapping("/{id}")
    public ResponseEntity<StaticGlossesDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody StaticGlossesDTO entity) {
        try {
            LOGGER.info("static update - init");
            Optional<StaticGlossesDTO> response = staticGlossesService.update(id, entity.toEntity());
            LOGGER.info("static update - end");
            return response.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (IllegalStateException e) {
            LOGGER.error("Error en proceso de actualizar glosa {}", id, e);
            throw new IlegalActionException(e.getMessage(), e);

        }  catch (Exception e) {
            LOGGER.error("Error al actualizar glosa", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(entity);
        }
    }

    @Operation(summary = "Eliminar una glosa")
    @PostMapping("/delete/{id}")
    public ResponseEntity<MessageDTO> delete(
            @PathVariable Long id, @RequestBody AprovalsDTO approval) {
        MessageDTO response;
        if (staticGlossesService.delete(id, approval.toEntity())) {
            response = new MessageDTO("true", "Objeto eliminado exitosamente");
            return ResponseEntity.ok(response);
        } else {
            response = new MessageDTO("false", "Objeto no eliminado");
            return ResponseEntity.ok(response);
        }
    }

    @Operation(summary = "Activar/Desactivar una glosa")
    @PostMapping("/change/{id}/status")
    public ResponseEntity<MessageDTO> enableOrDisable(
            @PathVariable Long id, @RequestBody AprovalsDTO approval) {
        MessageDTO response = new MessageDTO("400",
                "No se ha cambiado el estado de la glosa");
        try {
            LOGGER.info("static update - init");
            boolean result = staticGlossesService.toggleStatus(id, approval.toEntity());
            LOGGER.info("static update - end");
            if (result) {
                response = new MessageDTO("200",
                        "Estado de glosa actualizado exitosamente");
            } else {
                response = new MessageDTO("400",
                        "No se ha cambiado el estado de la glosa");
            }
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            LOGGER.error("No se ha cambiado el estado de la glosa", e);
            response = new MessageDTO(
                    String.valueOf(HttpStatus.BAD_REQUEST.value()), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

    }

    @Operation(summary = "Aprobar una glosa")
    @PostMapping("/{id}/approve")
    public ResponseEntity<StaticGlossesDTO> approve(
            @PathVariable Long id,
            @RequestBody AprovalsDTO approval) {
        try {
            return staticGlossesService.aproveAndActivate(id, approval.toEntity())
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (IllegalStateException e) {
            LOGGER.error("Error en proceso de actualizar glosa {}", id, e);
            throw new IlegalActionException(e.getMessage(), e);

        } catch (Exception e) {
            LOGGER.error("Error al aprobar glosa", e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

    }

    @Operation(summary = "Buscar una glosa por el nombre en estado activo")
    @PostMapping("/name")
    @ExceptionHandler(NoDataFoundException.class)
    public ResponseEntity<StaticGlossesDTO> getByName(@RequestParam("name") String name) {
        LOGGER.info("getName - init");
        Optional<StaticGlossesDTO> response = staticGlossesService.getGlossesByName(name);
        LOGGER.info("getName - end");
        return response.map(ResponseEntity::ok).orElse(
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

}
