package cl.bch.cloud.ms.fglosas.mantenedor.services;

import cl.bch.cloud.ms.fglosas.mantenedor.dtos.LoggerMaintainerDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.RequestSignatureSingsysDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.ResponseSignatureSingSysDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.SignaturesDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.AprobacionesEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.LogMantenedorEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.SignaturesEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.StatusEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.IlegalActionException;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.NoDataFoundException;
import cl.bch.cloud.ms.fglosas.mantenedor.repositories.SignaturesRepository;
import cl.bch.cloud.ms.fglosas.mantenedor.repositories.SignsysRepository;
import cl.bch.cloud.ms.fglosas.mantenedor.utils.StaticGlossesUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class SignaturesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SignaturesService.class);

    private final SignaturesRepository respository;
    private final StatusService statusService;
    private final LogMantenedorService loggerService;
    private final AprovalsService aprovalsService;
    private final SignsysRepository singSysRepository;

    public SignaturesService(SignaturesRepository signaturesRepository, StatusService statusService,
                             LogMantenedorService loggerService, AprovalsService aprovalsService,
                             SignsysRepository singSysRepository) {
        this.respository = signaturesRepository;
        this.statusService = statusService;
        this.loggerService = loggerService;
        this.aprovalsService = aprovalsService;
        this.singSysRepository = singSysRepository;
    }

    public List<SignaturesDTO> getAll() {
        return respository.findAll()
                .stream()
                .map(SignaturesDTO::fromEntity)
                .toList();
    }

    public Optional<SignaturesDTO> getById(long id) {
        try {
            return respository.findById(id)
                    .map(SignaturesDTO::fromEntity)
                    .or(() -> Optional.of(new SignaturesDTO(
                            null, "", "", "",
                            "", 0, null)
                    ));
        } catch (Exception e) {
            LOGGER.error("Error searching glosses by ID: {}", id, e);
            throw new NoDataFoundException(e);
        }
    }

    protected void validateAtLeastSignatureActive(StatusEntity entity, String message) {
        //si la lista esta vacia es xq no encontro ningun registro con el status del parametro
        if (respository.findAllWithStatus(entity.getId()).isEmpty()) {
            throw new IlegalActionException(message);
        }
    }

    protected void verifyStatusByRut(SignaturesEntity entity, String porSerApprovado) {
        SignaturesEntity param = new SignaturesEntity();
        param.setRut(entity.getRut());

        boolean result = respository.exists(Example.of(param));

        if (result) {
            throw new IlegalActionException(
                    "Ya existe, otra solicitud con ese rut en estado " + porSerApprovado);
        }
    }

    @Transactional
    public boolean delete(Long id, AprobacionesEntity aproval) {
        boolean result = false;

        if (!respository.existsById(id)) {
            return result;
        }
        //obtengo el id del status activo
        StatusEntity active = statusService.findByName(
                StaticGlossesUtils.ACTIVO);
        //Verifico firmas activas, si es vacio el array no quedan firmas no se puede eliminar
        validateAtLeastSignatureActive(active,
                StaticGlossesUtils.MESSAGE_AT_LEAST_SIGNATURE_ACTIVE);
        //busco la entidad por el id
        SignaturesEntity byDelete = respository.findById(id).get();
        //creo un log
        LogMantenedorEntity log = StaticGlossesUtils.createLogEntity(
                byDelete, StaticGlossesUtils.DELETE, "", aproval.getChecker());

        respository.deleteById(id);
        loggerService.create(LoggerMaintainerDTO.fromEntity(log));
        result = true;

        return result;
    }

    @Transactional
    public Optional<SignaturesDTO> create(SignaturesEntity entity) {
        //verifico que no exista otra solicitud con ese rut
        verifyStatusByRut(entity, StaticGlossesUtils.POR_SER_APPROVADO);
        //voy a buscar la firma
        getSignatureSignsys(entity);
        //asigno entidad por aprovar
        StatusEntity aproved = statusService.findByName(
                StaticGlossesUtils.POR_SER_APPROVADO);
        entity.setEstadoId(aproved);
        entity.setCreateAt(LocalDateTime.now());
        //guardo el registro
        respository.save(entity);
        //creo un registro de aprobacion
        AprobacionesEntity authorized = StaticGlossesUtils.createAprovalEntity(entity);
        aprovalsService.create(authorized);
        //creo un registro log
        LogMantenedorEntity log = StaticGlossesUtils.createLogEntity(
                entity, StaticGlossesUtils.CREATED, "", entity.getUserCreated());
        loggerService.create(LoggerMaintainerDTO.fromEntity(log));
        return Optional.of(SignaturesDTO.fromEntity(entity));

    }
    @Transactional
    public Optional<SignaturesDTO> update(Long id, String userUpdated) {

        if (!respository.existsById(id)) { //valido que exista el id
            return Optional.empty();
        }
        SignaturesEntity entity = respository.findById(id).get(); //obtengo el registro en db
        String valueOld = entity.getImagen();
        getSignatureSignsys(entity); //actualizo registro con nueva firma si es que existe
        respository.save(entity);
        //creo un registro log
        LogMantenedorEntity log = StaticGlossesUtils.createLogEntity(
                entity, StaticGlossesUtils.UPDATED, valueOld, userUpdated);
        loggerService.create(LoggerMaintainerDTO.fromEntity(log));
        return Optional.of(SignaturesDTO.fromEntity(entity));
    }

    @Transactional
    public boolean toggleStatus(Long id, AprobacionesEntity aprovals) {

        if (!respository.existsById(id)) {
            throw new NoDataFoundException("ID " + id + " no encontrado");
        }

        SignaturesEntity entity = respository.findById(id).orElseThrow(() ->
                new NoDataFoundException("No se pudo obtener la entidad para ID " + id));

        StatusEntity statusEntity;
        switch (entity.getEstadoId().getName()) {
            case StaticGlossesUtils.ACTIVO:
                statusEntity = statusService.findByName(StaticGlossesUtils.INACTIVO);
                break;
            case StaticGlossesUtils.INACTIVO:
                statusEntity = statusService.findByName(StaticGlossesUtils.ACTIVO);
                break;
            default:
                throw new IllegalStateException("Estado de glosa no permitido: " + entity.getEstadoId().getName());
        }
        entity.setEstadoId(statusEntity);
        LogMantenedorEntity logEntity = StaticGlossesUtils.toggleStatus(
                entity, StaticGlossesUtils.ESTADO, aprovals, statusEntity.getName());
        respository.save(entity);
        loggerService.create(LoggerMaintainerDTO.fromEntity(logEntity));
        return true;
    }

    protected void getSignatureSignsys(SignaturesEntity entity) {

        String token = singSysRepository.connect();
        //Armo el request
        RequestSignatureSingsysDTO request = RequestSignatureSingsysDTO.makeRequestFrom(entity, token);

        ResponseSignatureSingSysDTO response = singSysRepository.getSignature(request);

        if (StringUtils.isNotEmpty(response.SignatureImage())) {
            entity.setImagen(response.SignatureImage());
        }
    }

    public Optional<SignaturesDTO> approve(Long id, AprobacionesEntity aprovals) {
        //verifico que exista el id
        if (!respository.existsById(id)) {
            throw new IllegalArgumentException("id " + id + " not found");
        }
        //obtengo la entidad por el id
        SignaturesEntity entity = respository.findById(id).get();
        StatusEntity statusEntity;

        switch (entity.getEstadoId().getName()) {
            case StaticGlossesUtils.POR_SER_APPROVADO:
                statusEntity = getStatusSignature();
            break;

            case StaticGlossesUtils.APPROVADO:
                statusEntity = statusService.findByName(StaticGlossesUtils.INACTIVO);
            break;

            default:
                String msg = "Estado de glosa no permitido ".concat(entity.getEstadoId().getName());
                LOGGER.warn(msg + entity.toString());
                throw new IllegalStateException(msg);
        }

        entity.setEstadoId(statusEntity);
        LogMantenedorEntity logEntity = StaticGlossesUtils.toggleStatus(
                entity, StaticGlossesUtils.APPROVADO, aprovals, statusEntity.getName());

        respository.save(entity);

        LoggerMaintainerDTO dto = LoggerMaintainerDTO.fromEntity(logEntity);
        loggerService.create(dto);

        return Optional.of(SignaturesDTO.fromEntity(entity));
    }

    private StatusEntity getStatusSignature() {
        StatusEntity statusEntity;
        StatusEntity active = statusService.findByName(StaticGlossesUtils.ACTIVO);
        List<SignaturesEntity> entities = respository.findAllWithStatus(active.getId());
        if (entities.isEmpty()) {
            statusEntity = statusService.findByName(StaticGlossesUtils.ACTIVO);
        } else {
            statusEntity = statusService.findByName(StaticGlossesUtils.INACTIVO);
        }
        return statusEntity;
    }

    @Transactional
    public boolean toggleStatus(Long[] ids, AprobacionesEntity entity) {
        return Arrays.stream(ids)
                .allMatch(id -> toggleStatus(id, entity));
    }

    public SignaturesEntity getSignatureActive() {
        StatusEntity statusEntity = statusService.findByName(StaticGlossesUtils.ACTIVO);
        List<SignaturesEntity> results = respository.findAllWithStatus(statusEntity.getId());
        if (results.isEmpty()) {
            throw new NoDataFoundException("No se encontraron firmas activas");
        }
        return results.getFirst();
    }

    @Transactional
    public boolean enable(Long id, AprobacionesEntity aprovals) {

        if (!respository.existsById(id)) {
            throw new NoDataFoundException("ID " + id + " no encontrado");
        }

        SignaturesEntity entity = respository.findById(id).orElseThrow(() ->
                new NoDataFoundException("No se pudo obtener la entidad para ID " + id));

        StatusEntity statusEntity;
        if (StringUtils.equals(entity.getEstadoId().getName(), StaticGlossesUtils.INACTIVO)) {
            statusEntity = statusService.findByName(StaticGlossesUtils.ACTIVO);
        } else {
            throw new IllegalStateException("Estado de glosa no permitido: " + entity.getEstadoId().getName());
        }
        entity.setEstadoId(statusEntity);
        LogMantenedorEntity logEntity = StaticGlossesUtils.toggleStatus(
                entity, StaticGlossesUtils.ESTADO, aprovals, statusEntity.getName());
        respository.save(entity);
        loggerService.create(LoggerMaintainerDTO.fromEntity(logEntity));
        return true;
    }
}
