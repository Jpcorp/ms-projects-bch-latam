package cl.bch.cloud.ms.fglosas.mantenedor.dtos;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.io.Serializable;

@RecordBuilder
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record ResponseSignatureSingSysDTO(
    String Uname,
    String Udt,
    Integer Id,
    Integer RecStatus,
    Integer Person_Id,
    Integer Hst,
    String ScannedBy,
    String ScannedOn,
    String AuthorizedBy,
    String AuthorizedOn,
    String SignatureImage,
    String SignatureImage_New,
    String ScannedByUser_New,
    String ScannedOnDate_New,
    String SignatureBioData,
    String SignatureBioDataNew,
    String BioRegistrationForm,
    String BioEnrollmentDate,
    String BioDeviceType,
    String HostName) implements Serializable {
}
