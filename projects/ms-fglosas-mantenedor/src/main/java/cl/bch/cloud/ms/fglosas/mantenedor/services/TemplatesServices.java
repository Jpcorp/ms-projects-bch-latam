package cl.bch.cloud.ms.fglosas.mantenedor.services;

import cl.bch.cloud.dto.motor.plantillas.dtos.TemplatesRs;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.TransaccionDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.MarcaDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.CanalDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.DominioDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.LoggerMaintainerDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.NotificacionDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.RpAllTmpDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.RpIdTmpDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.TmpDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.AprobacionesEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.CampoEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.LogMantenedorEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.StatusEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.TemplatesEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.TmpChannelEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.TmpNotificationEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.MarcaEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.TransaccionEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.IlegalActionException;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.InvalidTemplateFieldsException;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.NoDataFoundException;
import cl.bch.cloud.ms.fglosas.mantenedor.repositories.TemplatesRepository;
import cl.bch.cloud.ms.fglosas.mantenedor.repositories.EngineTmpRespository;
import cl.bch.cloud.ms.fglosas.mantenedor.repositories.TmpNotificationRepository;
import cl.bch.cloud.ms.fglosas.mantenedor.repositories.CampoRepository;
import cl.bch.cloud.ms.fglosas.mantenedor.repositories.MarcaRepository;
import cl.bch.cloud.ms.fglosas.mantenedor.repositories.TransaccionRepository;
import cl.bch.cloud.ms.fglosas.mantenedor.utils.StaticGlossesUtils;
import cl.bch.cloud.ms.fglosas.mantenedor.utils.TemplateValidator;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TemplatesServices {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplatesServices.class);

    private final TemplatesRepository repository;
    private final StatusService statusService;
    private final AprovalsService aprovalsService;
    private final LogMantenedorService loggerService;
    private final EngineTmpRespository engineTmpRespository;
    private final TmpNotificationRepository notifyRespository;
    private final SignaturesService signaturesService;
    private final CampoRepository campoRepository;
    private final MarcaRepository marcaRepository;
    private final TransaccionRepository transaccionRepository;

    public TemplatesServices(
            TemplatesRepository repository,
            StatusService statusService,
            AprovalsService aprovalsService,
            LogMantenedorService loggerService,
            EngineTmpRespository engineTmpRespository,
            TmpNotificationRepository notifyRespository,
            SignaturesService signaturesService,
            CampoRepository campoRepository,
            MarcaRepository marcaRepository,
            TransaccionRepository transaccionRepository
    ) {

        this.repository = repository;
        this.statusService = statusService;
        this.aprovalsService = aprovalsService;
        this.loggerService = loggerService;
        this.engineTmpRespository = engineTmpRespository;
        this.notifyRespository = notifyRespository;
        this.signaturesService = signaturesService;
        this.campoRepository = campoRepository;
        this.marcaRepository = marcaRepository;
        this.transaccionRepository = transaccionRepository;
    }

    public List<RpAllTmpDTO> getAll() {

        List<TemplatesEntity> results = repository.findAll();
        return results.stream().map(RpAllTmpDTO::fromEntity).toList();
    }

    public Optional<RpIdTmpDTO> getByKey(long id) {
        try {
            TemplatesEntity result = repository.getTemplatesByIdAndUser(id);
            //si es comprobante agregar en la data el parametro firma
            if (StaticGlossesUtils.isVoucher(result, notifyRespository)) {
                StaticGlossesUtils.addParamSignature(result, signaturesService);
            } else {
                // 3. Asignar atributos adicionales si es necesario
                result.setAttr(result.getAttr()); // Asegúrate de que 'attr' esté definido en el contexto
            }
            TemplatesRs rpEngine = engineTmpRespository.getTemplateByAttr(result);
            return Optional.of(RpIdTmpDTO.fromEntity(result, rpEngine.getFile()));
        } catch (IlegalActionException e) {
            throw new IlegalActionException(e.getMessage(), e);
        } catch (Exception e) {
            throw new NoDataFoundException(e.getMessage(), e);
        }
    }


    public Optional<TmpDTO> createUpload(TemplatesEntity entity, MultipartFile file, String mark, String trx)
            throws IOException, InvalidTemplateFieldsException {
        validateMarkTrxStatus(mark, trx);
        validationFieldsTemlates(entity, file, mark, trx);
        //verifico que no exista otra solicitud con ese nombre y marca
        verifyByNameMark(entity, mark);

        //obtengo entidad por aprobar
        StatusEntity byAproved = statusService.findByName(StaticGlossesUtils.POR_SER_APPROVADO);
        entity.setEstado(byAproved);
        entity.setCreateAt(StaticGlossesUtils.now());

        //validar que el usuario puede cargar al canal por
        List<DominioDTO> arrays = obtenerDominiosConCanalesYNotificaciones();
        verifyNotificationById(arrays, entity.getConfig().getId());

        //obtengo el folder donde se va crear la plantilla
        String folder = getPathWithDomChannelNotify(entity.getConfig().getId());
        entity.setFolder(StringUtils.lowerCase(folder));
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        entity.setExtension(ext);
        entity.setEstadoDocto(StaticGlossesUtils.PENDTE_X_CARGAR);

        //si es comprobante agregar en la data el parametro firma
        if (StaticGlossesUtils.isVoucher(entity, notifyRespository)) {
            StaticGlossesUtils.addParamSignature(entity, signaturesService);
            entity.setVaucher(1);
        }

        MultipartFile newFile = StaticGlossesUtils.createVersionName(file, mark);
        //subo el registro en motor de plantillas
        engineTmpRespository.createEnginetmp(entity, newFile);
        TemplatesEntity saved = repository.save(entity);

        //creo un registro de aprobacion
        AprobacionesEntity authorized = StaticGlossesUtils.createTemplateEntity(saved);
        aprovalsService.create(authorized);

        //creo un registro log
        LogMantenedorEntity log = StaticGlossesUtils.createLogEntity(
                saved, StaticGlossesUtils.CREATED, "", entity.getUserCreated());

        loggerService.create(LoggerMaintainerDTO.fromEntity(log));

        return Optional.of(TmpDTO.fromEntity(saved, saved.getAttr()));
    }

    private void validateMarkTrxStatus(String mark, String trx) {
        TransaccionEntity TrxName = transaccionRepository.findByTrxIdMark(Long.parseLong(trx), mark);
        TemplatesEntity template = repository.findTopByTransaccionAndMarcaOrderByIdAsc(TrxName.getDescripcion(), mark);
        if(!Objects.isNull(template)) {
            throw new IllegalArgumentException(
                    "Existe una plantilla para ".concat(template.getMarca()).concat(" ")
                            .concat(template.getTransaccion()).concat("*").concat(template.getEstado().getName())
            );
        }
    }

    private void validationFieldsTemlates(TemplatesEntity entity, MultipartFile file, String mark, String trx)
            throws IOException, InvalidTemplateFieldsException {

        TransaccionEntity trxEntity = transaccionRepository.findByName(trx, mark);

        if (trxEntity == null) {
            throw new NoDataFoundException("No se encontró la glosa dinamica para validar campos.");
        }

        List<CampoEntity> templateFields = campoRepository.findCamposByTemplateIdAndMark(
                trxEntity.getId().toString(), mark);

        String htmlContent = new String(file.getBytes(), StandardCharsets.UTF_8);

        TemplateValidator validator = new TemplateValidator();
        validator.validateTemplateFieldsOrThrow(htmlContent, templateFields);
        validator.validateJsonTemplateFieldsOrThrow(htmlContent, entity.getAttr());

        entity.setTransaccion(trxEntity.getDescripcion());
    }



    public void verifyNotificationById(List<DominioDTO> arrays, Long id) {
        boolean found = arrays.stream()
                .flatMap(dominioDTO -> dominioDTO.getCanales().stream())
                .flatMap(canalDTO -> canalDTO.getNotificaciones().stream())
                .anyMatch(notificacionDTO -> notificacionDTO.getId() == id);

        if (!found) {
            throw new IlegalActionException("La id notificacion no es valida");
        }
    }

    public List<DominioDTO> obtenerDominiosConCanalesYNotificaciones() {
        //obtener la entidad notificaciones y sus canales y dominios
        List<TmpNotificationEntity> notify = notifyRespository.findAllWithChannelDomain();

        Map<String, List<TmpChannelEntity>> canalesPorDominio = notify.stream()
                .map(TmpNotificationEntity::getCanal)
                .filter(c -> c != null && c.getDominio() != null)
                .collect(Collectors.groupingBy(c -> c.getDominio().getName()));

        return canalesPorDominio.entrySet().stream().map(entry -> {
            String nombreDominio = entry.getKey();

            List<CanalDTO> canalesDTO = entry.getValue().stream().map(canal -> {
                List<NotificacionDTO> notificaciones = notifyRespository.findByCanalId(canal.getId())
                        .stream()
                        .map(n -> new NotificacionDTO(n.getId(), n.getName(), n.getPath()))
                        .collect(Collectors.toList());
                return new CanalDTO(canal.getName(), notificaciones, canal.getId());
            }).collect(Collectors.toList());

            return  new DominioDTO(nombreDominio, canalesDTO);
        }).collect(Collectors.toList());
    }

    private String getPathWithDomChannelNotify(Long id) {
        return notifyRespository.getFolderNotifyId(id);
    }

    @Transactional
    public Optional<TmpDTO> updateUpload(Long id, TemplatesEntity entity, MultipartFile file) throws IOException {

        //verifico que el id existe
        if (!repository.existsById(id)) {
            return Optional.empty();
        }
        //debe solicitar quien actualiza
        if (StringUtils.isEmpty(entity.getUserUpdated())) {
            throw new IllegalStateException(
                    "debe ingresar usuario quien actualiza");
        }

        List<DominioDTO> arrays = obtenerDominiosConCanalesYNotificaciones();

        //obtengo el registro en bd
        TemplatesEntity original = repository.getTemplatesByIdAndUser(id);

        TransaccionEntity trxEntity = transaccionRepository.findByDescMark(
                original.getTransaccion(), original.getMarca());
        //valido los campos del archvio con los enviados
        validationFieldsTemlates(original, file, original.getMarca(), trxEntity.getId().toString());

        StatusEntity statusEntity;
        TemplatesEntity result;
        switch (original.getEstado().getName()) {
            case StaticGlossesUtils.POR_SER_APPROVADO:
                //actualizo y no creo copia
                statusEntity = statusService.findByName(StaticGlossesUtils.POR_SER_APPROVADO);
                original.setEstado(statusEntity);
                result = updateWithoutOrWithCopy(original, entity, file, 0, arrays);
                return Optional.of(TmpDTO.fromEntity(result, ""));

            case StaticGlossesUtils.ACTIVO:
                statusEntity = statusService.findByName(StaticGlossesUtils.POR_SER_APPROVADO);
                TemplatesEntity copy = getCopyTemplatesEntity(entity, original, statusEntity);
                verifyStatusByNameFolder(copy);
                result = updateWithoutOrWithCopy(copy, entity, file, 1, arrays);
                return Optional.of(TmpDTO.fromEntity(result, ""));

            default:
                String msg = "Estado de glosa no permitido ".concat(entity.getEstado().getName());
                throw new IllegalStateException(msg);
        }
    }

    @NotNull
    private static TemplatesEntity getCopyTemplatesEntity(
            TemplatesEntity entity, TemplatesEntity original, StatusEntity statusEntity) {

        TemplatesEntity copy = StaticGlossesUtils.createCopyTemplate(original, entity);
        copy.setEstado(statusEntity);
        String nombreOriginal = original.getName();
        String extension = FilenameUtils.getExtension(nombreOriginal);
        String baseNombre = nombreOriginal.substring(0, nombreOriginal.lastIndexOf('.'));
        // Buscar si el nombre contiene una versión tipo -vN
        Pattern pattern = Pattern.compile("(.+)-v(\\d+)$");
        Matcher matcher = pattern.matcher(baseNombre);

        String nuevoNombre;
        if (matcher.find()) {
            String nombreBase = matcher.group(1);
            int versionActual = Integer.parseInt(matcher.group(2));
            int nuevaVersion = versionActual + 1;
            nuevoNombre = nombreBase + "-v" + nuevaVersion + "." + extension;
        } else {
            // Si no tiene versión, empezar con v2
            nuevoNombre = baseNombre + "-v2." + extension;
        }
        copy.setName(nuevoNombre);
        return copy;
    }

    private TemplatesEntity updateWithoutOrWithCopy(TemplatesEntity templates, TemplatesEntity entity,
                                                    MultipartFile file, int flag, List<DominioDTO> arrays) {
        templates.setUpdateAt(LocalDate.now());
        templates.setUserUpdated(entity.getUserUpdated());
        entity.setConfig(templates.getConfig());
        verifyNotificationById(arrays, entity.getConfig().getId());

        TemplatesEntity result;
        if (flag == StaticGlossesUtils.isCopy) { //es una actualizacion con copia
            templates.setVaucher(1);
            engineTmpRespository.createEnginetmp(templates, file);
            //actualizo el registro en bd
            result = repository.save(templates);

        } else { //es una actualizacion sin copia
            //actualizo attr
            templates.setVaucher(1);
            templates.setAttr(entity.getAttr());
            engineTmpRespository.createEnginetmp(templates, file);
            //actualizo el registro en bd
            result = repository.save(templates);
        }
        //creo un registro de aprobacion
        AprobacionesEntity authorized = StaticGlossesUtils.createTemplateEntity(templates);
        aprovalsService.create(authorized);
        //creo un registro log
        LogMantenedorEntity log = StaticGlossesUtils.createLogEntity(
                templates, StaticGlossesUtils.UPDATED, templates.getFolder().concat(templates.getName()),
                templates.getUserUpdated());

        loggerService.create(LoggerMaintainerDTO.fromEntity(log));
        return result;
    }

    public void verifyByNameMark(TemplatesEntity entity, String mark) {
        boolean exists = repository.getNameMark(entity.getName(), mark);
        if (exists) {
            throw new IlegalActionException(
                    "Ya existe, otra solicitud con ese nombre "
                            + entity.getName().concat(" ").concat(mark)
            );
        }
    }

    public void verifyStatusByNameFolder(@NotNull TemplatesEntity entity) {
        boolean result = repository.getByNameStatus(entity.getName(),
                entity.getEstado().getId(), entity.getFolder());

        if (result) {
            throw new IlegalActionException(
                    "Ya existe, Una solicitud en proceso " + entity.getEstado().getName());
        }
    }

    @Transactional
    public Optional<TmpDTO> approve(Long id, AprobacionesEntity aprovals) {
        //verifico que exista el id
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("id " + id + " not found");
        }
        //obtengo la entidad por el id
        TemplatesEntity entity = repository.findById(id).get();
        StatusEntity statusEntity;
        switch (entity.getEstado().getName()) {
            case StaticGlossesUtils.POR_SER_APPROVADO:
                statusEntity = statusService.findByName(StaticGlossesUtils.ACTIVO);
                saveAndDeleteEntity(entity, statusEntity);
                break;
            case StaticGlossesUtils.INACTIVO:
                statusEntity = statusService.findByName(StaticGlossesUtils.ACTIVO);
                break;
            case StaticGlossesUtils.ACTIVO:
                statusEntity = statusService.findByName(StaticGlossesUtils.POR_SER_APPROVADO);
                break;
            default:
                String msg = "Estado de glosa no permitido ".concat(entity.getEstado().getName());
                LOGGER.warn(msg.concat(entity.toString()));
                throw new IllegalStateException(msg);
        }
        entity.setEstado(statusEntity);

        LogMantenedorEntity logEntity = StaticGlossesUtils.toggleStatus(
                entity, StaticGlossesUtils.APPROVADO, aprovals, statusEntity.getName(),
                StaticGlossesUtils.IDENTITY_TEMPLATES);

        repository.save(entity);

        LoggerMaintainerDTO dto = LoggerMaintainerDTO.fromEntity(logEntity);
        loggerService.create(dto);

        return Optional.of(TmpDTO.fromEntity(entity, ""));
    }

    public void saveAndDeleteEntity(TemplatesEntity entity, StatusEntity statusEntity) {
        //obtener el nombre de la entidad
        String nombEntity = entity.getName();
        //obtengo la extension
        String ext = FilenameUtils.getExtension(nombEntity);
        //obtengo el nombre del archivo
        String nombre = nombEntity.substring(0, nombEntity.lastIndexOf('.'));
        //Patterns
        Pattern pattern = Pattern.compile("(.+)-v(\\d+)$");
        Matcher matcher = pattern.matcher(nombre);

        if (matcher.find()) {
            String baseName = matcher.group(1);
            int verActual = Integer.parseInt(matcher.group(2));
            int verPrevious = verActual - 1;

            if (verPrevious >= StaticGlossesUtils.isCopy) {

                String nombreAnterior = baseName + "-v" + verPrevious + "." + ext;
                TemplatesEntity original = repository.getEntityByNameStatusFolder(
                        nombreAnterior, statusEntity.getId(), entity.getFolder());

                original.setEstado(statusService.findByName(StaticGlossesUtils.INACTIVO));
                repository.save(original);

                entity.setEstado(statusEntity);
                repository.save(entity);
            }
        } else {
            // Si no tiene versión, simplemente guarda
            entity.setEstado(statusEntity);
            repository.save(entity);
        }
    }

    public Optional<RpIdTmpDTO> getByAttr(TemplatesEntity param) {
        try {
            StatusEntity statusEntity = statusService.findByName(StaticGlossesUtils.ACTIVO);
            // 1. Establecer criterios de búsqueda usando Specification
            Specification<TemplatesEntity> spec = StaticGlossesUtils.getByAttr(param, statusEntity);
            // 2. Buscar la entidad que cumpla con los criterios
            TemplatesEntity entity = repository.findBy(spec, query ->
                            query.first().orElseThrow(
                                    () -> new NoDataFoundException(StaticGlossesUtils.MESSAGE_NOT_FOUND_TEMPLATE)));

            //si es comprobante agregar en la data el parametro firma
            if (StaticGlossesUtils.isVoucher(entity, notifyRespository)) {
                entity.setAttr(param.getAttr());
                StaticGlossesUtils.addParamSignature(entity, signaturesService);
            } else {
                // 3. Asignar atributos adicionales si es necesario
                entity.setAttr(param.getAttr()); // Asegúrate de que 'attr' esté definido en el contexto
            }

            // 4. Obtener el recurso asociado a la plantilla
            TemplatesRs rpEngine = engineTmpRespository.getTemplateByAttr(entity);
            // 5. Construir y retornar el DTO con los datos obtenidos
            return Optional.of(RpIdTmpDTO.fromEntity(entity, rpEngine.getFile()));

        } catch (Exception e) {
            // Manejo de errores con mensaje claro
            throw new NoDataFoundException("Error al consultar servicio plantilla: " + e.getMessage(), e);
        }
    }

    public List<TransaccionDTO> obtenerTransaccionesPorMarca(String idMarca) {
        List<TransaccionEntity> transacciones = transaccionRepository.findByMarca_Id(idMarca);
        return transacciones.stream().map(TransaccionDTO::fromEntity).toList();
    }

    public List<MarcaDTO> getAllMarcas() {
        List<MarcaEntity> marcas = marcaRepository.findAll();
        return marcas.stream().map(MarcaDTO::fromEntity).toList();
    }
}
