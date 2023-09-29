package uk.gov.justice.laa.crime.hardship.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.hardship.exception.ValidationException;
import uk.gov.justice.laa.crime.hardship.model.*;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class HardshipValidationService {

    public void checkHardship(final ApiPerformHardshipRequest apiPerformHardshipRequest) {
        validateHardshipReviewStatus(apiPerformHardshipRequest);
        validateHardshipReviewNewWorkReason(apiPerformHardshipRequest);
        validateSolicitorDetails(apiPerformHardshipRequest);
        validateFundingSources(apiPerformHardshipRequest);
        validateDeniedIncome(apiPerformHardshipRequest);
        validateExpenditure(apiPerformHardshipRequest);
        validateProgressionItems(apiPerformHardshipRequest);
    }

    private void validateHardshipReviewStatus(ApiPerformHardshipRequest apiPerformHardshipRequest) {
        if(hardshipStatusIsCompleteWithoutReviewDate(apiPerformHardshipRequest)){
            throw new ValidationException("Review Date must be entered for completed hardship");
        }
    }

    private void validateHardshipReviewNewWorkReason(ApiPerformHardshipRequest apiPerformHardshipRequest) {
        if (isNull(apiPerformHardshipRequest.getHardshipMetadata().getReviewReason())) {
            throw new ValidationException("Review Reason must be entered for hardship");
        }
    }

    private void validateSolicitorDetails(ApiPerformHardshipRequest apiPerformHardshipRequest) {
        var solicitorCosts = apiPerformHardshipRequest.getHardship().getSolicitorCosts();
        var solicitorRate = solicitorCosts.getRate();
        var solicitorHours = solicitorCosts.getHours();
        if (solicitorRateSpecifiedWithoutSolicitorHours(solicitorRate, solicitorHours)) {
            throw new ValidationException("Solicitor Number of Hours must be entered when Solicitor Hourly Rate is specified");
        }
    }

    private void validateFundingSources(ApiPerformHardshipRequest apiPerformHardshipRequest) {
        List<OtherFundingSource> fundingSources = nullSafe(apiPerformHardshipRequest.getHardship().getOtherFundingSources());
        fundingSources.forEach(funding -> {
            if (fundingDescriptionWithoutFundingAmountOrDueDate(funding))
                throw new ValidationException("Amount and Date Expected must be entered for each detail in section "+funding.getDescription());
        });
    }

    private void validateDeniedIncome(ApiPerformHardshipRequest apiPerformHardshipRequest) {
        List<DeniedIncome> deniedIncomes = nullSafe(apiPerformHardshipRequest.getHardship().getDeniedIncome());
        deniedIncomes.forEach(deniedIncome -> {
            if(deniedIncomeWithoutAmountOrFrequencyOrReasonNote(deniedIncome))
                throw new ValidationException("Amount, Frequency, and Reason must be entered for each detail in section "+deniedIncome.getDescription());
        });
    }

    private void validateExpenditure(ApiPerformHardshipRequest apiPerformHardshipRequest) {
        List<ExtraExpenditure> expenditures = nullSafe(apiPerformHardshipRequest.getHardship().getExtraExpenditure());
        expenditures.forEach(expenditure -> {
            if(expenditureWithoutAmountOrFrequencyOrReasonCode(expenditure))
                throw new ValidationException("Amount, Frequency, and Reason must be entered for each detail in section "+expenditure.getDescription());

        });
    }

    private void validateProgressionItems(ApiPerformHardshipRequest apiPerformHardshipRequest) {
        var progressionItems = apiPerformHardshipRequest.getHardshipMetadata().getProgressItems();
        progressionItems.forEach(progression -> {
            if(progressionItemWithoutRequiredDateOrResponseOrDateTaken(progression))
                throw new ValidationException("Date Taken, Response Required, and Date Required must be entered for each Action Taken in section Review Progress");

        });
    }

    private static boolean progressionItemWithoutRequiredDateOrResponseOrDateTaken(HardshipProgress progression) {
        return (nonNull(progression.getAction()) && nonNull(progression.getAction().getAction())) &&
                ((isNull(progression.getDateRequired()) || (isNull(progression.getResponse()) ||
                        StringUtils.isEmpty(progression.getResponse().getResponse())))) || isNull(progression.getDateTaken());
    }

    private static boolean expenditureWithoutAmountOrFrequencyOrReasonCode(ExtraExpenditure expenditure) {
        return (StringUtils.isNotEmpty(expenditure.getDescription()) &&
                (nullSafe(expenditure.getAmount()).compareTo(BigDecimal.ZERO) == 0) ||
                        (isNull(expenditure.getFrequency()) || StringUtils.isEmpty(expenditure.getFrequency().getCode())) ||
                        (nonNull(expenditure.getReasonCode()) && StringUtils.isEmpty(expenditure.getReasonCode().getReason())));
    }

    private static boolean hardshipStatusIsCompleteWithoutReviewDate(ApiPerformHardshipRequest apiPerformHardshipRequest) {
        return ((apiPerformHardshipRequest.getHardshipMetadata().getReviewStatus() == HardshipReviewStatus.COMPLETE)
                && isNull(apiPerformHardshipRequest.getHardship().getReviewDate()));
    }

    private static boolean solicitorRateSpecifiedWithoutSolicitorHours(BigDecimal solicitorRate, Integer solicitorHours) {
        return (nullSafe(solicitorRate).compareTo(BigDecimal.ZERO) == 1)
                && (nullSafe(solicitorHours) == 0);
    }

    private static boolean fundingDescriptionWithoutFundingAmountOrDueDate(OtherFundingSource funding) {
        return StringUtils.isNotEmpty(funding.getDescription()) &&
                ((nullSafe(funding.getAmount()).compareTo(BigDecimal.ZERO) == 0) || isNull(funding.getDueDate()));
    }

    private static boolean deniedIncomeWithoutAmountOrFrequencyOrReasonNote(DeniedIncome deniedIncome) {
        return nonNull(deniedIncome.getItemCode()) && StringUtils.isNotEmpty(deniedIncome.getItemCode().getCode()) &&
                ((nullSafe(deniedIncome.getAmount()).compareTo(BigDecimal.ZERO) == 0)  ||
                        ((isNull(deniedIncome.getFrequency()) || StringUtils.isEmpty(deniedIncome.getFrequency().getCode())) ||
                        StringUtils.isEmpty(deniedIncome.getReasonNote())));
    }

    private static BigDecimal nullSafe(BigDecimal value) {
        return Optional.ofNullable(value).orElse(BigDecimal.ZERO);
    }

    private static Integer nullSafe(Integer value) {
        return Optional.ofNullable(value).orElse(0);
    }

    private static List nullSafe(List value) {
        return Optional.ofNullable(value).orElse(List.of());
    }

}
