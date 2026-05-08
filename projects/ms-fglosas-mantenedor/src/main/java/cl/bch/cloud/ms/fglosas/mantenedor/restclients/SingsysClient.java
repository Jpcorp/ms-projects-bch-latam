package cl.bch.cloud.ms.fglosas.mantenedor.restclients;

import cl.bch.cloud.ms.fglosas.mantenedor.dtos.RequestTokenSingSysDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.ResponseSignatureSingSysDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient(name = "SingsysClient", url = "${rest.endpoints.singsys.url}")
public interface SingsysClient {

    @RequestMapping(method = RequestMethod.POST, value = "/api/login/authenticate")
    String getToken(@RequestBody RequestTokenSingSysDTO request);

    @RequestMapping(method = RequestMethod.GET, value = "/api/Signatures/ByPersonIdNumber")
    ResponseSignatureSingSysDTO getSignature(
            @RequestParam("PersonIdType_ID") int personIdTypeId,
            @RequestParam("PersonIdNumber") String personIdNumber,
            @RequestParam("api_key") String apiKey,
            @RequestHeader("AuthToken") String authToken
    );
}