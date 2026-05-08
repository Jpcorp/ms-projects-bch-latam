package cl.bch.cloud.ms.fglosas.mantenedor.repositories;

import cl.bch.cloud.ms.fglosas.mantenedor.dtos.RequestSignatureSingsysDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.RequestTokenSingSysDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.dtos.ResponseSignatureSingSysDTO;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.IlegalActionException;
import cl.bch.cloud.ms.fglosas.mantenedor.exceptions.NoResponseException;
import cl.bch.cloud.ms.fglosas.mantenedor.restclients.SingsysClient;
import feign.Request;
import feign.RetryableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SignsysRepositoryTest {

    private SingsysClient client;
    private SignsysRepository repository;
    private String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1bmlxdWVfbmFtZSI6ImZnbG9zYXMiLCJBcHBWZXJzaW9uIjo" +
            "ic3RyaW5nIiwiQXBwSWQiOiJzdHJpbmciLCJBdWRpZW5jZVRva2VuR2VuZXJhdGVkIjoiNWRuOUxENWxqMm13M0NsSkxNaFMiLCJJ" +
            "c3N1ZXJUb2tlbkdlbmVyYXRlZCI6IjVkbjlMRDVsajJtdzNDbEpMTWhTIiwicm9sZSI6WyJGQEZ1bGxBUEkiLCJGQExlZ2FsQX" +
            "V0aG9yaXplciIsIkZATGVnYWxTdXBlcnZpc29yIl0sIm5iZiI6MTc1MjUxMDQ3MSwiZXhwIjoxNzUyNTEyMjcxLCJpYXQiOjE3" +
            "NTI1MTA0NzEsImlzcyI6IjVkbjlMRDVsajJtdzNDbEpMTWhTIiwiYXVkIjoiNWRuOUxENWxqMm13M0NsSkxNaFMifQ.BvqMvIR" +
            "IIKDQJps365ze3Um5I4FkLBIJf8Oocn-KscQ";

    @BeforeEach
    void setUp() {
        client = mock(SingsysClient.class);
        repository = new SignsysRepository(client);
        repository.appId = "appId";
        repository.appVersion = "1.0";
        repository.username = "user";
        repository.password = "pass";
    }

    private RequestSignatureSingsysDTO getRequest() {
        RequestSignatureSingsysDTO dto = new RequestSignatureSingsysDTO(
                0, "10329642-0", "0", token );
        return dto;
    }

    private ResponseSignatureSingSysDTO getResponseSignatureSingSys() {
        String img = "iVBORw0KGgoAAAANSUhEUgAAAU0AAAB1AQMAAADwYCHzAAAABGdBTUEAALGPC/xhBQAAAAZQTFRFAAAA////pdmf3QAAA" +
                "AlwSFlzAAAOwgAADsIBFShKgAAAA3NJREFUWMPt10Fr1EAUB/CU1q6gsoKXKmoUD+JJRNCqS0fxg6hQ2qOCJ2npWC0sSqHgWTr" +
                "H4ifw4GFXWqqINhdBD8JuXWn0YlYiJmMm83xJVsh2s/PWi6fNocnhR3bIvP97UwsGvoZ0SId0SIf0v1AO0EjugqSadxQjqe" +
                "IdRdPmwFTvtdM1ZH+MVF0qg/67EDMNpdOhQNGW70KcPEiSvgoGpbET+Bn9QVFpt3yIkiefoiHzOqZFUu4kFBfqkhQaEQQJX" +
                "aGoBCGzt5LUh5oEL3myKdoCJtNy1UU0zFMXeEZVUWXLPHV0h0qWfDnTAqoa9yp5oc8hLFUHojsc6vy5iZ5VoOwaLmGLF32D" +
                "HNW3YyxUju89Ck0gqMyoqsC6mSovwp3i+HFjKoYhFovEAPJ12S6Q+S2QKcW0luQ6QaNGAArpPI8YQX2kYQV0HdqcoEHDhf" +
                "AYqNEsBIGBug0P5EvMAlQp6ggPoqY1Auo0TR2IQqTtNFqugTZqDYgiTLfrUFQkFBuBtlPqmCgTEAXgh1xQtMZqCcVmVEvXY" +
                "6CMM4hccEtZsQiC+h7s2Bmt9ad6gXMIPPiajqTecdBFsQBHHaxqzeOCcZCnWvOmdQN3dSBaumBjAHXStnrGQZ4q3Z4qLSob1" +
                "PYc6+3xXVRt6xVLCT1P0jh2YlG5J/SBL3dZT2F1U3XcdWYOr1ZGviJ9ZaThvLs1u//4pfNvkK6YqJTKW60vTz27W7csxkx0" +
                "rhk7q/WTFl4782PcQGHufrz09MG5E9bM1U3e27Ty9MXou5HK43PW1S/jB3nLSMNS/VrlyLRljS3cYSUjbVc3rk0emZ4YK2Mr" +
                "Kxvp5w/vNycvT4vvGJWCVpSnS9WlxSuTt8BxCo8Z3XTPo/NIhUjGrYnqU2tnnlw8ikkRhVM+R8PW2r7xi4uYFExV1Uzdmevjs" +
                "y8zWjZS2Nm6vt9LksIKD095uvFpwnIyWjSL8vTtx4mDyXfCbD8k6LtDjVgkJwceUrNg+RCkFDNOzYKn1b90EQgq1tKt9/UdRl" +
                "LsmfjLri4+f++iv1M6StIyg194cyxO0lkGP/H2epykukPvT9H0B4dtlGWgKZbzN2iPiAGoKsO6suzCQ143VXAT+wovPIvsojEs" +
                "WJYNfX5+F+Vwus9/Dz00y8kAFDu6vgH9rxyN+pxb+1A2GA3+jfL+EsI/oIYfCYyGLXwAAAAASUVORK5CYII=";

        ResponseSignatureSingSysDTO response = new ResponseSignatureSingSysDTO(
                "CLTConvert", "2023-06-09T22:20:54.43", 645124, 0, 663703,
                0, "CLT/0100914468", "2013-06-04T00:00:00", "CLT/0100914468",
                "2013-06-04T00:00:00", img, null, null,
                null, null, null,
                null, null, null, null);

        return response;
    }

    private Request createFakeRequest() {
        return Request.create(
                Request.HttpMethod.GET,
                "http://localhost",
                java.util.Collections.emptyMap(),
                (Request.Body) null,
                (feign.RequestTemplate) null
        );
    }
    @Test
    void connect_shouldReturnToken_whenValidTokenReturned() {
        when(client.getToken(any(RequestTokenSingSysDTO.class))).thenReturn("\"abc123\"");

        String token = repository.connect();

        assertEquals("abc123", token);
    }

    @Test
    void connect_shouldThrowIlegalActionException_whenTokenIsEmpty() {
        when(client.getToken(any(RequestTokenSingSysDTO.class))).thenReturn("");

        IlegalActionException exception = assertThrows(IlegalActionException.class, () -> repository.connect());
        assertEquals("Invalid token", exception.getMessage());
    }

    @Test
    void connect_shouldThrowNoResponseException_whenRetryableExceptionThrown() {
        NoResponseException exception;
        when(client.getToken(any(RequestTokenSingSysDTO.class)))
                .thenThrow(new RetryableException(500, "Timeout", Request.HttpMethod.GET, new Date(), createFakeRequest()));
                        exception = assertThrows(NoResponseException.class, () -> repository.connect());
        assertEquals("Service not response", exception.getMessage());
    }




    @Test
    void getSignature_shouldReturnResponse_whenClientReturnsValidResponse() {
        RequestSignatureSingsysDTO request = getRequest();
        ResponseSignatureSingSysDTO response = getResponseSignatureSingSys();

        when(client.getSignature(
                anyInt(), anyString(), anyString(), anyString())).thenReturn(response);

        ResponseSignatureSingSysDTO result = repository.getSignature(request);

        assertEquals("CLTConvert", result.Uname());
    }


    @Test
    void getSignature_shouldThrowNoResponseException_whenRetryableExceptionThrown() {
        RequestSignatureSingsysDTO request = getRequest();

        NoResponseException exception;
        when(client.getSignature(anyInt(), anyString(), anyString(), anyString()))
                .thenThrow(new RetryableException(500, "Timeout", Request.HttpMethod.GET, new Date(), createFakeRequest()));
                        exception = assertThrows(NoResponseException.class, () -> repository.getSignature(request));
        assertEquals("Service Signature not response", exception.getMessage());
    }
}
