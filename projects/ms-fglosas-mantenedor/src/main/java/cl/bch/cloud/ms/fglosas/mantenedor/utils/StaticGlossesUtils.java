package cl.bch.cloud.ms.fglosas.mantenedor.utils;

import cl.bch.cloud.ms.fglosas.mantenedor.dtos.ReqEngineTemplateDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.AprobacionesEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.StaticGlossesEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.LogMantenedorEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.SignaturesEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.TemplatesEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.StatusEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.entities.TmpNotificationEntity;
import cl.bch.cloud.ms.fglosas.mantenedor.repositories.TmpNotificationRepository;
import cl.bch.cloud.ms.fglosas.mantenedor.services.SignaturesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class StaticGlossesUtils {

    public static final String INACTIVO = "INACTIVO";
    public static final String ACTIVO = "ACTIVO";
    public static final String POR_SER_APPROVADO = "POR APROBAR";
    public static final String APPROVADO = "APROBADO";
    public static final String CARGADO = "CARGADO";
    public static final String PENDTE_X_CARGAR = "PENDIENTE POR CARGAR";

    public static final String CREATED = "CREAR";
    public static final String UPDATED = "ACTUALIZAR";
    public static final String DELETE = "BORRAR";

    public static final String IDENTITY_GLOSSES = "GLOSAS_ESTATICAS";
    public static final String IDENTITY_SIGNATURE = "FIRMAS";
    public static final String IDENTITY_TEMPLATES = "PLANTILLAS";
    public static final String SIGNATURE = "firma";
    public static final String SIGNATORY = "nombre_firmante";

    public static final String MESSAGE_AT_LEAST_SIGNATURE_ACTIVE = "Debe quedar al menos una firma activa";
    public static final String MESSAGE_NOT_FOUND_TEMPLATE = "Verificar plantilla y/o estado incorrecto " +
            "con los criterios especificados.";
    public static final String MESSAGE_NOT_FOUND_TRX_NAME = "No se encontró glosa dinámica " +
            "con los criterios especificados.";
    public static final String ESTADO = "CAMBIO ESTADO";
    public static final String BARRA = "/";
    public static final String VOUCHER = "comprobante";
    public static final String HTML = "html";
    public static final int isCopy = 1;

    private static final ZoneId zoneId = ZoneId.of("America/Santiago");


    public static AprobacionesEntity createAuthorized(
            @NotNull StaticGlossesEntity glossesEntity, String checker) {
        AprobacionesEntity aprobacionesEntity = new AprobacionesEntity();
        aprobacionesEntity.setIdentity(IDENTITY_GLOSSES);
        aprobacionesEntity.setIdentityId(glossesEntity.getId());
        aprobacionesEntity.setChecker(checker);
        aprobacionesEntity.setComentario("");
        aprobacionesEntity.setCreateAt(ZonedDateTime.now(zoneId).toLocalDateTime());
        return aprobacionesEntity;
    }

    public static LogMantenedorEntity createLogEntity(
            @NotNull StaticGlossesEntity entity, String action, String valueOld) {
        LogMantenedorEntity log = new LogMantenedorEntity();
        log.setIdentity(IDENTITY_GLOSSES);
        log.setIdentityId(entity.getId());
        log.setAction(action);
        log.setValueNew(entity.getName().concat("|").concat(entity.getValue()));
        log.setValueOld(valueOld);
        log.setUserResposability(entity.getUserCreated());
        log.setCreatedAt(ZonedDateTime.now(zoneId).toLocalDateTime());
        return log;
    }

    public static LogMantenedorEntity createLogEntity(
            @NotNull SignaturesEntity entity, String action, String valueOld, String responsability) {
        LogMantenedorEntity log = new LogMantenedorEntity();
        log.setIdentity(IDENTITY_SIGNATURE);
        log.setIdentityId(entity.getId());
        log.setAction(action);
        if (StringUtils.equals(UPDATED, action)) {
            log.setValueNew(entity.getImagen());
        } else {
            log.setValueNew(entity.getRut().concat("|").concat(entity.getName()));
        }
        log.setValueOld(valueOld);
        log.setUserResposability(responsability);
        log.setCreatedAt(ZonedDateTime.now(zoneId).toLocalDateTime());
        return log;
    }

    public static LogMantenedorEntity createLogEntity(
            @NotNull TemplatesEntity entity, String action, String valueOld, String responsability) {
        LogMantenedorEntity log = new LogMantenedorEntity();
        log.setIdentity(IDENTITY_TEMPLATES);
        log.setIdentityId(entity.getId());
        log.setAction(action);
        log.setValueNew(entity.getFolder().concat("|").concat(entity.getName()));
        log.setValueOld(valueOld);
        log.setUserResposability(responsability);
        log.setCreatedAt(ZonedDateTime.now(zoneId).toLocalDateTime());
        return log;
    }

    public static AprobacionesEntity createAprovalEntity(
            @NotNull SignaturesEntity entity) {
        AprobacionesEntity aproval = new AprobacionesEntity();
        aproval.setIdentity(IDENTITY_SIGNATURE);
        aproval.setIdentityId(entity.getId());
        aproval.setCreateAt(ZonedDateTime.now(zoneId).toLocalDateTime());
        return aproval;
    }


    public static void fromParam(@NotNull AprobacionesEntity update,
                                 @NotNull AprobacionesEntity aprobacion) {
        update.setChecker(aprobacion.getChecker());
        update.setComentario(aprobacion.getComentario());
        update.setFechaAprobacion(ZonedDateTime.now(zoneId).toLocalDateTime());
    }

    public static LogMantenedorEntity deleteLogEntity(
            StaticGlossesEntity entity, AprobacionesEntity approval) {
        LogMantenedorEntity log = new LogMantenedorEntity();
        log.setIdentity(IDENTITY_GLOSSES);
        log.setIdentityId(entity.getId());
        log.setAction(DELETE);
        log.setUserResposability(approval.getChecker());
        log.setCreatedAt(ZonedDateTime.now(zoneId).toLocalDateTime());
        log.setValueNew(DELETE);
        log.setValueOld(entity.getName().concat("|").concat(entity.getValue()));
        return log;
    }

    public static void copyOf(@NotNull StaticGlossesEntity newi,
                              @NotNull StaticGlossesEntity old) {
        old.setUserUpdated(newi.getUserCreated());
        newi.setUserCreated(old.getUserCreated());
        newi.setUserUpdated(old.getUserUpdated());
        old.setUpdateAt(newi.getCreateAt());
        newi.setCreateAt(old.getCreateAt());
        newi.setUpdateAt(old.getUpdateAt());
    }

    public static String quitarSufijoCopia(String texto) {
        if (texto != null && texto.toLowerCase(Locale.getDefault()).endsWith("-copia")) {
            return texto.substring(0, texto.length() - 6).trim();
        }
        return texto;
    }

    public static boolean terminaConCopia(String texto) {
        return texto != null && texto.toLowerCase(Locale.getDefault()).endsWith("-copia");
    }

    public static void updateAproval(AprobacionesEntity update, AprobacionesEntity param) {
        update.setIdentity(param.getIdentity());
        update.setChecker(param.getChecker());
        update.setComentario(param.getComentario());
        update.setFechaAprobacion(param.getFechaAprobacion());
    }

    public static LogMantenedorEntity toggleStatus(
            StaticGlossesEntity entity, String action, AprobacionesEntity aprovals, String value) {
        LogMantenedorEntity result = new LogMantenedorEntity();
        result.setIdentity(IDENTITY_GLOSSES);
        result.setIdentityId(entity.getId());
        result.setAction(action);
        result.setValueNew(value);
        result.setValueOld(entity.getEstadoId().getName());
        result.setUserResposability(aprovals.getChecker());
        result.setCreatedAt(ZonedDateTime.now(zoneId).toLocalDateTime());
        return result;
    }

    public static LogMantenedorEntity toggleStatus(
            SignaturesEntity entity, String action, AprobacionesEntity aprovals, String value) {
        LogMantenedorEntity result = new LogMantenedorEntity();
        result.setIdentity(IDENTITY_SIGNATURE);
        result.setIdentityId(entity.getId());
        result.setAction(action);
        result.setValueNew(value);
        result.setValueOld(entity.getEstadoId().getName());
        result.setUserResposability(aprovals.getChecker());
        result.setCreatedAt(ZonedDateTime.now(zoneId).toLocalDateTime());
        return result;
    }

    public static LogMantenedorEntity toggleStatus(
            TemplatesEntity entity, String action, AprobacionesEntity aprovals, String value, String identity) {
        LogMantenedorEntity result = new LogMantenedorEntity();
        result.setIdentity(identity);
        result.setIdentityId(entity.getId());
        result.setAction(action);
        result.setValueNew(value);
        result.setValueOld(entity.getEstado().getName());
        result.setUserResposability(aprovals.getChecker());
        result.setCreatedAt(ZonedDateTime.now(zoneId).toLocalDateTime());
        return result;
    }

    public static AprobacionesEntity createTemplateEntity(@NotNull TemplatesEntity entity) {
        AprobacionesEntity aproval = new AprobacionesEntity();
        aproval.setIdentity(IDENTITY_TEMPLATES);
        aproval.setIdentityId(entity.getId());
        aproval.setCreateAt(ZonedDateTime.now(zoneId).toLocalDateTime());
        return aproval;
    }


    public static TemplatesEntity createCopyTemplate(
            @NotNull TemplatesEntity original, @NotNull TemplatesEntity entity) {

        return new TemplatesEntity(
                null, original.getExtension(), original.getName(), original.getFolder(),
                original.getMarca(), original.getTransaccion(), original.getConfig(),
                original.getEstado(), PENDTE_X_CARGAR, entity.getVaucher(),
                entity.getAttr(), original.getUserCreated(), entity.getUserUpdated(), original.getCreateAt(),
                original.getUpdateAt());
    }

    public static LocalDate now() {
        return ZonedDateTime.now(zoneId).toLocalDate();
    }

    public static void addParamSignature(TemplatesEntity entity, SignaturesService signaturesService)
            throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        // Obtener los JSON como JsonNode
        JsonNode atributosNode = ReqEngineTemplateDTO.getJsonNode(entity.getAttr(), "atributos");
        JsonNode dataNode = ReqEngineTemplateDTO.getJsonNode(entity.getAttr(), "data");

        // Buscar la firma activa
        SignaturesEntity signatures = signaturesService.getSignatureActive();
        String dataConFirma;
        String firmante;
        if (StringUtils.equals(entity.getExtension(), HTML)) {
            String src = "data:image/png;base64,".concat(signatures.getImagen());
            dataConFirma = ReqEngineTemplateDTO.addNodeSignature(dataNode.toString(),
                    SIGNATURE, src);

            firmante = ReqEngineTemplateDTO.addNodeSignature(dataConFirma,
                    SIGNATORY, signatures.getName());
        } else {
            // Agregar nodo de firma al JSON de data
            dataConFirma = ReqEngineTemplateDTO.addNodeSignature(dataNode.toString(),
                    SIGNATURE, signatures.getImagen());

            firmante = ReqEngineTemplateDTO.addNodeSignature(dataConFirma,
                    SIGNATORY, signatures.getName());
        }
        // Convertir el JSON modificado de data a JsonNode
        JsonNode dataConFirmanteNode = mapper.readTree(firmante);
        // Crear un nuevo ObjectNode para fusionar ambos
        ObjectNode combinado = mapper.createObjectNode();
        combinado.set("atributos", atributosNode);
        combinado.set("data", dataConFirmanteNode);
        // Convertir el resultado a String
        String jsonFinal = mapper.writeValueAsString(combinado);
        // Aquí puedes hacer lo que necesites con jsonFinal (guardar, enviar, etc.)
        entity.setAttr(jsonFinal);
    }

    public static Specification<TemplatesEntity> getByAttr(@NotNull TemplatesEntity entity, StatusEntity statusEntity) {
        Specification<TemplatesEntity> spec = Specification.where(null);

        if (StringUtils.isNotEmpty(entity.getMarca()) && StringUtils.isNotEmpty(entity.getTransaccion())) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("marca"), entity.getMarca()));

            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("transaccion"), entity.getTransaccion()));
        }

        if (ObjectUtils.isNotEmpty(statusEntity)) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("estado"), statusEntity));
        }

        return spec;
    }

    public static boolean isVoucher(@NotNull TemplatesEntity entity, TmpNotificationRepository notifyRespository) {
        TmpNotificationEntity voucher = notifyRespository.findById(
                entity.getConfig().getId()).orElse(new TmpNotificationEntity());
        String result = StringUtils.lowerCase(voucher.getName());
        return StringUtils.equals(result, VOUCHER);
    }

    /**
     * Agrega la versión -v1 al nombre si no la tiene.
     * Si ya tiene una versión (por ejemplo, pagos-v1.html), no modifica nada.
     *
     * @param nombreArchivo Nombre del archivo (ej: "pagos.html" o "pagos-v1.html")
     * @param mark
     * @return Nombre del archivo con versión si era necesario
     */
    public static String versionarNombreSiEsNecesario(String nombreArchivo, String mark) {
        if (nombreArchivo == null || !nombreArchivo.contains(".")) {
            throw new IllegalArgumentException("El nombre del archivo no es válido.");
        }

        String extension = FilenameUtils.getExtension(nombreArchivo);
        String nombreSinExtension = nombreArchivo.substring(0, nombreArchivo.lastIndexOf('.'));

        // Verificar si ya tiene una versión tipo -vN
        Pattern pattern = Pattern.compile("(.+)-v(\\d+)$");
        Matcher matcher = pattern.matcher(nombreSinExtension);

        if (matcher.find()) {
            // Ya tiene versión, no se modifica
            return nombreArchivo;
        } else {
            // No tiene versión, se agrega -v1
            return nombreSinExtension + "-" + StringUtils.lowerCase(mark) + "-v1." + extension;
        }
    }

    public static MultipartFile createVersionName(@NotNull MultipartFile file, String mark) throws IOException {
        String nombreOriginal = file.getOriginalFilename();
        String nuevoNombre = versionarNombreSiEsNecesario(nombreOriginal, mark);

        return new CustomMultipartFileUtils(
                file.getBytes(),
                nuevoNombre,
                nuevoNombre,
                file.getContentType()
        );
    }
}
