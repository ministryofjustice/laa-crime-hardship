package uk.gov.justice.laa.crime.hardship.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ErrorDTO {
    String traceId;
    String code;
    String message;
}
