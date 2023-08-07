package uk.gov.justice.laa.crime.hardship.dto;

import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.crime.hardship.model.HardshipReviewDetail;
import uk.gov.justice.laa.crime.hardship.model.HardshipReviewProgress;
import uk.gov.justice.laa.crime.hardship.model.NewWorkReason;
import uk.gov.justice.laa.crime.hardship.model.SolicitorCosts;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


public record HardshipReviewDTO(Integer id,
                                Integer cmuId,
                                String notes,
                                String decisionNotes,
                                LocalDateTime reviewDate,
                                String reviewResult,
                                BigDecimal disposableIncome,
                                BigDecimal disposableIncomeAfterHardship,
                                NewWorkReason newWorkReason,
                                SolicitorCosts solicitorCosts,
                                @NotNull HardshipReviewStatus reviewStatus,
                                List<HardshipReviewDetail> reviewDetails,
                                List<HardshipReviewProgress> reviewProgressItems,
                                String courtType) {
}
