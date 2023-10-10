package uk.gov.justice.laa.crime.hardship.dto.maat_api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SolicitorCosts {
    private BigDecimal rate;
    private Integer hours;
    private BigDecimal vat;
    private BigDecimal disbursements;
    private BigDecimal estimatedTotal;
}
