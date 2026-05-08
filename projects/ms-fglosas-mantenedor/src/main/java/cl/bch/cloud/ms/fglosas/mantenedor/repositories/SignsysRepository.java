package cl.bch.cloud.ms.fglosas.mantenedor.repositories;

import cl.bch.cloud.ms.fglosas.mantenedor.dtos.RequestSignatureSingsysDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.RequestTokenSingSysDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.ResponseSignatureSingSysDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.IlegalActionException;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.NoResponseException;
import cl.bch.cloud.ms.fglosas.mantenedor.restclients.SingsysClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RetryableException;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Observed
@Repository("SignsysRepository")
@RequiredArgsConstructor
public class SignsysRepository {

    private final SingsysClient client;
    private static final Logger LOGGER = LoggerFactory.getLogger(SignsysRepository.class);

    @Value("${rest.endpoints.singsys.app.id}")
    protected String appId;
    @Value("${rest.endpoints.singsys.app.version}")
    protected String appVersion;
    @Value("${rest.endpoints.singsys.username}")
    protected String username;
    @Value("${rest.endpoints.singsys.password}")
    protected String password;

    private ObjectMapper objectMapper = new ObjectMapper();

    public String connect() {
        RequestTokenSingSysDTO request = new RequestTokenSingSysDTO(appId, appVersion, username, password);

        String token;
        try {
            LOGGER.info(objectMapper.writeValueAsString(request));
            token = client.getToken(request);
            if (StringUtils.isEmpty(token)) {
                throw new IlegalActionException(
                        "Invalid token");
            }
        } catch (RetryableException e) {
            LOGGER.error("Error al obtener el token", e);
            throw new NoResponseException("Service not response", e);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error al procesar la respuesta del servicio", e);
            throw new NoResponseException("Service can not processing response", e);
        }
        return token.replace('"', ' ').trim();
    }
    public ResponseSignatureSingSysDTO getSignature (RequestSignatureSingsysDTO request) {
        ResponseSignatureSingSysDTO result;
        try {
            result = client.getSignature(request.PersonIdType_Id(),
                    request.PersonIdNumber(), request.authToken(), request.authToken());
            if (result == null) {
                throw new IlegalActionException(
                        "El rut ingresado no tiene firma");
            }
        } catch (RetryableException e) {
            LOGGER.error("Error al obtener firma", e);
            throw new NoResponseException("Service Signature not response", e);
        }
        return result;
    }
}
