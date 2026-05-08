package cl.bch.cloud.ms.fglosas.mantenedor.services;

import cl.bch.cloud.ms.fglosas.mantenedor.dtos.LoggerMaintainerDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.StaticGlossesDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.AprobacionesEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.StaticGlossesEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.StatusEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.TypeGlossesEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.LogMantenedorEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.NoDataFoundException;
import cl.bch.cloud.ms.fglosas.mantenedor.repositories.StaticGlossesRepository;
import cl.bch.cloud.ms.fglosas.mantenedor.utils.StaticGlossesUtils;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StaticGlossesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticGlossesService.class);

    private final StaticGlossesRepository staticGlossesRepository;
    private final AprovalsService aprovalsService;
    private final LogMantenedorService loggerService;
    private final StatusService statusService;
    private final TypeGlossesService typeService;

    public StaticGlossesService(
            StaticGlossesRepository staticGlossesRepository,
            AprovalsService aprovalsService,
            LogMantenedorService loggerService, StatusService statusService, TypeGlossesService typeService) {
        this.staticGlossesRepository = staticGlossesRepository;
        this.aprovalsService = aprovalsService;
        this.loggerService = loggerService;
        this.statusService = statusService;
        this.typeService = typeService;
    }

    public Optional<StaticGlossesDTO> getGlossesById(long id) {
        try {
            return staticGlossesRepository.findById(id)
                    .map(StaticGlossesDTO::fromEntity)
                    .or(() -> Optional.of(new StaticGlossesDTO(
                            null, "", "",
                            "", "", 0, 0,
                            null, null
                    )));
        } catch (Exception e) {
            LOGGER.error("Error searching glosses by ID: {}", id, e);
            throw new NoDataFoundException(e);
        }
    }

    public List<StaticGlossesDTO> getAllGlosses() {
        return staticGlossesRepository.findAll()
                .stream()
                .map(StaticGlossesDTO::fromEntity)
                .toList();
    }

    @Transactional
    public Optional<StaticGlossesDTO> create(StaticGlossesEntity entity) {
        if (StringUtils.isEmpty(entity.getUserCreated())) {
            throw new IllegalStateException(
                    "debe ingresar usuario quien crea glosa");
        }
        //obtener el id en estado por aprobar
        StatusEntity statusEntity = statusService.findByName(StaticGlossesUtils.POR_SER_APPROVADO);
        TypeGlossesEntity typeGlossesEntity = typeService.findByName(StaticGlossesUtils.IDENTITY_GLOSSES);

        entity.setEstadoId(statusEntity); //seteo el estado
        entity.setCreateAt(LocalDateTime.now()); //seteo el estado
        entity.setTipoGlosa(typeGlossesEntity); //seteo el tipo de glosa
        //antes de guardar buscar otro valor que tenga mismo valor y estado
        List<StaticGlossesEntity> entities = searchByCriteria(
                entity.getName(), 0);

        entities.forEach(param -> {
            if (StringUtils.equals(param.getNameToLowerCase(), entity.getNameToLowerCase())) {
                throw new IllegalStateException("Glosa ya existe");
            }
        });
        //guardo el registro
        staticGlossesRepository.save(entity);

        //creo un registro de autorizacionn
        AprobacionesEntity authorized = StaticGlossesUtils.createAuthorized(entity, "");
        aprovalsService.create(authorized);

        boolean existeCopia = StaticGlossesUtils.terminaConCopia(entity.getName());

        LogMantenedorEntity log;
        if(existeCopia){
             log = StaticGlossesUtils.createLogEntity(
                    entity, StaticGlossesUtils.UPDATED, "");
        }else{
             log = StaticGlossesUtils.createLogEntity(
                    entity, StaticGlossesUtils.CREATED, "");
        }

        loggerService.create(LoggerMaintainerDTO.fromEntity(log));

        return Optional.of(StaticGlossesDTO.fromEntity(entity));

    }

    @Transactional
    public Optional<StaticGlossesDTO> update(Long id, StaticGlossesEntity updated) {

        //estado por aprobar
        StatusEntity toBeAproved = statusService.findByName(
                StaticGlossesUtils.POR_SER_APPROVADO);

        if (!staticGlossesRepository.existsById(id)) {
            return Optional.empty();
        }

        if (StringUtils.isEmpty(updated.getUserUpdated())) {
            throw new IllegalStateException(
                    "debe ingresar usuario quien actualiza ");
        }

        if (staticGlossesRepository.existsByName(
                updated.getName(), toBeAproved.getId())) {
            throw new IllegalStateException(
                    "Se encontraron múltiples glosas por aprobar: " + updated.getName());
        }

        //obtengo el registro en base de datos
        StaticGlossesEntity register = staticGlossesRepository.findById(id).get();
        //actualizo valor, quien actualiza, estado, fecha actualizacion
        register.setValue(updated.getValue());
        register.setEstadoId(toBeAproved);
        register.setUserUpdated(updated.getUserUpdated());
        register.setUpdateAt(LocalDateTime.now());

        staticGlossesRepository.save(register);

        AprobacionesEntity authorized = StaticGlossesUtils.createAuthorized(
                register, "");
        aprovalsService.create(authorized);

        LogMantenedorEntity log = StaticGlossesUtils.createLogEntity(
                register, StaticGlossesUtils.UPDATED, register.getValue());
        loggerService.create(LoggerMaintainerDTO.fromEntity(log));

        return Optional.of(StaticGlossesDTO.fromEntity(register));
    }

    protected List<StaticGlossesEntity> searchByCriteria(@NonNull String name, long status) {
        Specification<StaticGlossesEntity> spec = Specification.where(null);
        if (!name.isEmpty()) {
            spec = spec.and((
                    root,
                    query, cb) -> cb.like(
                    cb.lower(root.get("name")), "%" + Strings.toLowerCase(name) + "%"));
        }
        if (status != 0) {
            spec = spec.and(
                    (root, query, cb)
                            -> cb.equal(root.get("estadoId").get("id"), status));
        }

        return staticGlossesRepository.findAll(spec);
    }

    @Transactional
    public boolean toggleStatus(Long id, AprobacionesEntity aprovals) {
        boolean result = false;
        if (!staticGlossesRepository.existsById(id)) {
            return result;
        }
        if (StringUtils.isEmpty(aprovals.getChecker()) || StringUtils.isEmpty(aprovals.getComentario())) {
            throw new IllegalStateException("Aprobador y/o comentario no puede ser vacío");
        }
        StaticGlossesEntity entity = staticGlossesRepository.findById(id).get();
        StatusEntity statusEntity;

        switch (entity.getEstadoId().getName()) {
            case StaticGlossesUtils.ACTIVO:
                statusEntity = statusService.findByName(StaticGlossesUtils.INACTIVO);
                entity.setEstadoId(statusEntity);
            break;

            case StaticGlossesUtils.INACTIVO:
                statusEntity = statusService.findByName(StaticGlossesUtils.ACTIVO);
                entity.setEstadoId(statusEntity);
            break;
            default:
                throw new IllegalStateException("Estado de glosa no permitido");
        }
        LogMantenedorEntity logEntity = StaticGlossesUtils.toggleStatus(
               entity, StaticGlossesUtils.ESTADO, aprovals, statusEntity.getName());
        staticGlossesRepository.save(entity);

        LoggerMaintainerDTO dto = LoggerMaintainerDTO.fromEntity(logEntity);
        loggerService.create(dto);
        return true;
    }

    @Transactional
    public boolean delete(Long id, AprobacionesEntity approval) {
        boolean result = false;
        if (!staticGlossesRepository.existsById(id)) {
            return Boolean.FALSE;
        }
        // busco es estado por aprobar
        StatusEntity toBeAproved = statusService.findByName(
                StaticGlossesUtils.POR_SER_APPROVADO);

        //busco la entidad por el id
        StaticGlossesEntity entity = staticGlossesRepository.findById(id).get();

        if (staticGlossesRepository.existsByName(
                entity.getName(), toBeAproved.getId())) {

            staticGlossesRepository.deleteById(id);

            LogMantenedorEntity logEntity = StaticGlossesUtils.deleteLogEntity(entity, approval);
            LoggerMaintainerDTO dto = LoggerMaintainerDTO.fromEntity(logEntity);
            loggerService.create(dto);
            result = Boolean.TRUE;
        }
        return result;
    }

    /**
     * Metodo para aprobar una glosa
     *
     * @param id       el id de la glosa aprobar
     * @param aprovals datos del aprobador
     * @return retorna la glosa aprobada
     */
    @Transactional
    public Optional<StaticGlossesDTO> glossesAproved(Long id, AprobacionesEntity aprovals) {
        //verifico si exite el registro en la tabla de glosas
        if (!staticGlossesRepository.existsById(id)) {
            throw new IllegalArgumentException("id " + id + " not found");
        }
        if (StringUtils.isEmpty(aprovals.getChecker()) || StringUtils.isEmpty(aprovals.getComentario())) {
            throw new IllegalStateException("Aprobador y/o comentario no puede ser vacío");
        }
        StatusEntity activated = statusService.findByName(
                StaticGlossesUtils.ACTIVO);

        //buscar en la tabla de aprobaciones el registro que se hizo
        AprobacionesEntity register = aprovalsService.findByIdentityWithStatus(
                StaticGlossesUtils.IDENTITY_GLOSSES, id);

        //seteo los parametros checker, comentario, fechaAprobacion, status
        StaticGlossesUtils.fromParam(register, aprovals);

        //obtengo el registro de la tabla glosas estaticas
        StaticGlossesEntity glossesNew = staticGlossesRepository
                .findById(register.getIdentityId()).get();

        StatusEntity aproved = statusService.findByName(
                StaticGlossesUtils.APPROVADO);

        //Busco una lista de todas las glosas con ese nombre y estado activo
        List<StaticGlossesEntity> array = searchByCriteria(
                glossesNew.getName(), aproved.getId());
        //valido que no exista mas de una glosa en estado aprobado
        if (!array.isEmpty()) {
            throw new IllegalArgumentException("There is more than one gloss in an approved state " + id);
        }
        //del arreglo obtengo quien lo USER_CREATED y CREATE_AT
//        StaticGlossesUtils.copyOf(glossesNew, array.getFirst()); REVISAR
        //cambio la glosa aprobado
        glossesNew.setEstadoId(activated);
        glossesNew.setUserCreated(aprovals.getChecker());
        //elimino la glosa que existia antes
//        this.delete(array.getFirst().getId());                   REVISAR
        staticGlossesRepository.save(glossesNew);
        LogMantenedorEntity log = StaticGlossesUtils.createLogEntity(
                glossesNew, StaticGlossesUtils.APPROVADO, "");
        loggerService.create(LoggerMaintainerDTO.fromEntity(log));
        return Optional.of(StaticGlossesDTO.fromEntity(glossesNew));
    }

    @Transactional
    public Optional<StaticGlossesDTO> aproveAndActivate(Long id, AprobacionesEntity aprovals) {

        //verifico si exite el registro en la tabla de glosas
        if (!staticGlossesRepository.existsById(id)) {
            throw new IllegalArgumentException("id " + id + " not found");
        }
        boolean existeCopia = StaticGlossesUtils.terminaConCopia(aprovals.getComentario());

        StaticGlossesEntity glosaCopia = null;
        StaticGlossesEntity glosaOriginal = null;
        if (existeCopia) {

            StatusEntity activated = statusService.findByName(
                    StaticGlossesUtils.ACTIVO);
            StatusEntity deactivated = statusService.findByName(
                    StaticGlossesUtils.INACTIVO);

            glosaCopia = staticGlossesRepository.
                    findByNameCopied(aprovals.getComentario(), 3);

            String nombreSinSufijo = StaticGlossesUtils.quitarSufijoCopia(glosaCopia.getName());
            glosaOriginal = staticGlossesRepository.
                    findByNameCopied(nombreSinSufijo, 2);

            glosaCopia.setName(nombreSinSufijo);
            glosaCopia.setEstadoId(activated);
            glosaOriginal.setEstadoId(deactivated);

            staticGlossesRepository.save(glosaCopia);
            staticGlossesRepository.delete(glosaOriginal);

            LogMantenedorEntity log = StaticGlossesUtils.createLogEntity(
                    glosaCopia, StaticGlossesUtils.APPROVADO, glosaOriginal.getValue());
            loggerService.create(LoggerMaintainerDTO.fromEntity(log));
            //TODO: IR A ACTUALIZAR REDIS.PARA ACTIVAR LA GLOSA EDITADA

            return Optional.of(StaticGlossesDTO.fromEntity(glosaCopia));
        }else {
            //TODO: IR A ACTUALIZAR REDIS PARA DEJAR ACTIVA LA NUEVA GLOSA.
            return this.glossesAproved(id, aprovals);
        }
    }

    public Optional<StaticGlossesDTO> getGlossesByName(String name) {

        try {
            StatusEntity activated = statusService.findByName(
                    StaticGlossesUtils.ACTIVO); //obtengo el estado activo

            //buscar la glosa por el nombre y el estado activo
            List<StaticGlossesEntity> array = staticGlossesRepository.findByName(
                    StringUtils.lowerCase(name).trim(), activated.getId());

            if (array.size() >= 0) {
                return Optional.of(StaticGlossesDTO.fromEntity(array.getFirst()));
            } else {
                return Optional.empty();
            }

        } catch (Exception e) {
            String message = "Error searching glosses by name: ".concat(name);
            throw new NoDataFoundException(message, e);
        }
    }
}


