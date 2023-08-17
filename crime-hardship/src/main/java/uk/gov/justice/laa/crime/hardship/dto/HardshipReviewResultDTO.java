package uk.gov.justice.laa.crime.hardship.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class HardshipReviewResultDTO {

    private String hardshipReviewResult;
    private BigDecimal hardshipSummary;
    private BigDecimal disposableIncome;
    private BigDecimal postHardshipDisposableIncome;

}
