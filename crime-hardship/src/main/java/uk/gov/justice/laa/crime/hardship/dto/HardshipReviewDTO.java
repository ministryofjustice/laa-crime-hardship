package uk.gov.justice.laa.crime.hardship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.crime.hardship.model.HardshipReviewDetail;
import uk.gov.justice.laa.crime.hardship.model.HardshipReviewProgress;
import uk.gov.justice.laa.crime.hardship.model.NewWorkReason;
import uk.gov.justice.laa.crime.hardship.model.SolicitorCosts;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HardshipReviewDTO {
    private Integer id;
    private Integer cmuId;
    private String notes;
    private String decisionNotes;
    private LocalDateTime reviewDate;
    private String reviewResult;
    private BigDecimal disposableIncome;
    private BigDecimal disposableIncomeAfterHardship;
    private NewWorkReason newWorkReason;
    private SolicitorCosts solicitorCosts;
    private HardshipReviewStatus reviewStatus;
    private List<HardshipReviewDetail> reviewDetails;
    private List<HardshipReviewProgress> reviewProgressItems;
    private String courtType;
}



