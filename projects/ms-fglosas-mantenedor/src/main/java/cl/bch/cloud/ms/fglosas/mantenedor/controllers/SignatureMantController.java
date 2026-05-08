package cl.bch.cloud.ms.fglosas.mantenedor.controllers;

import cl.bch.cloud.ms.fglosas.mantenedor.dtos.AprovalsDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.MessageDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.SignaturesDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.services.SignaturesService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Tag(name = "Firmas", description = "Tiene como función principal gestionar y actualizar " +
        "el estado de las firmas dentro del sistema")
@Observed
@RestController
@RequestMapping(path = "/signature/", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class SignatureMantController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SignatureMantController.class);

    @Autowired
    private final SignaturesService service;

    @Operation(summary = "Obtener firma por ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "firma encontrada"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "firma no encontrada",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
            })
    @GetMapping("/{id}")
    public ResponseEntity<SignaturesDTO> getById(@PathVariable long id) {
        LOGGER.info("SignatureById - init");
        Optional<SignaturesDTO> response = service.getById(id);
        LOGGER.info("SignatureById - end");
        return response.map(ResponseEntity::ok).orElse(
                ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Devuelve todas las Firmas")
    @GetMapping("/")
    public ResponseEntity<List<SignaturesDTO>> getAll() {
        LOGGER.info("SignatureGetAll - init");
        List<SignaturesDTO> response = service.getAll();
        LOGGER.info("SignatureGetAll - end");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Crear una nueva firma")
    @PostMapping("/")
    public ResponseEntity<SignaturesDTO> create(@Valid @RequestBody SignaturesDTO entity) {
        LOGGER.info("SignatureCreate - init");
        Optional<SignaturesDTO> result = service.create(entity.toEntity());
        LOGGER.info("SignatureCreate - end");
        return ResponseEntity.status(HttpStatus.CREATED).body(result.get());
    }

    @Operation(summary = "Actualizar una firma existente")
    @PutMapping("/{id}/{userUpdated}")
    public ResponseEntity<SignaturesDTO> update(
            @PathVariable Long id, @PathVariable String userUpdated) {
        Optional<SignaturesDTO> response = Optional.empty();
        try {
            LOGGER.info("signature update - init");
            response = service.update(id, userUpdated);
            LOGGER.info("static update - end");
            return ResponseEntity.ok().body(response.get());
        } catch (IllegalStateException e) {
            LOGGER.error("Error en el proceso de actualizar firma {}", id, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(summary = "Eliminar una firma")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestBody AprovalsDTO aproval) {
        LOGGER.info("signature delete - init");
        boolean deleted = service.delete(id, aproval.toEntity());
        LOGGER.info("signature delete - end");
        return deleted ? ResponseEntity.ok().build() :
                ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "Aprobar una firma")
    @PostMapping("/{id}/approve")
    public ResponseEntity<SignaturesDTO> approve(
            @PathVariable Long id,
            @RequestBody AprovalsDTO approval) {
        try {
            return service.approve(id, approval.toEntity())
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (Exception e) {
            LOGGER.error("Error al aprobar glosa", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }
    @Operation(summary = "Activar/Desactivar una firma")
    @PostMapping("/change/{id}/{slave}/status")
    public ResponseEntity<MessageDTO> enableOrDisable(
            @PathVariable Long id, @PathVariable long slave, @RequestBody AprovalsDTO approval) {
        MessageDTO response;
        LOGGER.info("signature enableOrDisable - init");
        List<Long> ids = Arrays.asList(id, slave);
        try {
            Long[] idArray = ids.stream().toArray(Long[]::new);
            boolean result = service.toggleStatus(idArray, approval.toEntity());
            LOGGER.info("signature enableOrDisable - end");
            if (result) {
                response = new MessageDTO("200", "Estado de glosa actualizado exitosamente");
                return ResponseEntity.ok(response);
            } else {
                response = new MessageDTO("400", "No se ha cambiado el estado de la glosa");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            LOGGER.error("Error al cambiar estado de glosa", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(summary = "Activar una firma")
    @PostMapping("/change/{id}/status")
    public ResponseEntity<MessageDTO> enable (
            @PathVariable Long id, @RequestBody AprovalsDTO approval) {
        MessageDTO response;
        LOGGER.info("signature enable - init");
        try {
            boolean result = service.enable(id, approval.toEntity());
            LOGGER.info("signature enable - end");
            if (result) {
                response = new MessageDTO("200", "Estado de glosa actualizado exitosamente");
                return ResponseEntity.ok(response);
            } else {
                response = new MessageDTO("400", "No se ha cambiado el estado de la glosa");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            LOGGER.error("Error al cambiar estado de glosa", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}
