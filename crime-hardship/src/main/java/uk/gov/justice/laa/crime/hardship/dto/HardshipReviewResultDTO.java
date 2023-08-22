package uk.gov.justice.laa.crime.hardship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HardshipReviewResultDTO {

    private String hardshipReviewResult;
    private BigDecimal hardshipSummary;
    private BigDecimal disposableIncome;
    private BigDecimal disposableIncomeAfterHardship;

}
