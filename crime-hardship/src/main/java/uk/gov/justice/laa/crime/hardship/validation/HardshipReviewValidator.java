package uk.gov.justice.laa.crime.hardship.validation;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewDTO;
import uk.gov.justice.laa.crime.hardship.exeption.ValidationException;
import uk.gov.justice.laa.crime.hardship.model.HardshipReviewDetail;
import uk.gov.justice.laa.crime.hardship.model.HardshipReviewProgress;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewStatus;

import java.util.Optional;

@Slf4j
@Service
public class HardshipReviewValidator {

    public static final String MSG_INVALID_DATE = "Date Taken, Response Required, and Date Required must be entered for each Action Taken in section Review Progress";
    public static final String MSG_INVALID_DETAIL_IN_SECTION = "Amount, Frequency, and Reason must be entered for each detail in section ";
    public static final String MSG_INVALID_FIELD = "Solicitor Number of Hours must be entered when Solicitor Hourly Rate is specified";
    public static final String MSG_INVALID_REVIEW_DATE = "The Review Date must be entered for completed hardship";
    public static final String MSG_INVALID_REVIEW_REASON = "The Review Reason must be entered for hardship";

    public static Optional<Void> validateHardshipReviewProgressItem(HardshipReviewProgress hardshipReviewProgress) {
        if (hardshipReviewProgress.dateRequested() == null
                || hardshipReviewProgress.progressResponse() == null
                || hardshipReviewProgress.dateRequired() == null) {
            throw new ValidationException(MSG_INVALID_DATE);
        }
        return Optional.empty();
    }

    public static Optional<Void> validateHardshipReviewExpenditureItem(HardshipReviewDetail hrDetailType) {
        if ((hrDetailType.getDetailCode().getDescription() != null) &&
                (hrDetailType.getAmount() == null
                        || hrDetailType.getFrequency() == null
                        || (hrDetailType.getDetailReason() == null || hrDetailType.getDetailReason().getId() == null))) {
            throw new ValidationException(MSG_INVALID_DETAIL_IN_SECTION + hrDetailType.getDescription());
        }
        return Optional.empty();
    }

    public static Optional<Void> validateHardshipReviewIncomeItem(HardshipReviewDetail hrDetailType) {
        if ((hrDetailType.getDetailCode().getDescription() != null) && (hrDetailType.getAmount() == null
                || hrDetailType.getFrequency() == null
                || hrDetailType.getReasonNote() == null)) {
            throw new ValidationException(MSG_INVALID_DETAIL_IN_SECTION + hrDetailType.getDescription());
        }
        return Optional.empty();
    }

    public static Optional<Void> validateHardshipReviewFundingItem(HardshipReviewDetail hrDetailType) {
        if (hrDetailType.getAmount() == null || hrDetailType.getDateDue() == null) {
            throw new ValidationException(MSG_INVALID_DETAIL_IN_SECTION + hrDetailType.getDescription());
        }
        return Optional.empty();
    }

    public static Optional<Void> validateHardshipMandatoryFields(HardshipReviewDTO hardshipReviewDTO) {
        if (hardshipReviewDTO.getNewWorkReason().code() == null) {
            throw new ValidationException(MSG_INVALID_REVIEW_REASON);
        }

        if ((hardshipReviewDTO.getSolicitorCosts() != null && hardshipReviewDTO.getSolicitorCosts().getSolicitorRate() != null)
                && (hardshipReviewDTO.getSolicitorCosts().getSolicitorHours() == null)) {
            throw new ValidationException(MSG_INVALID_FIELD);
        }
        return Optional.empty();
    }

    public static Optional<Void> validateCompletedHardship(HardshipReviewDTO hardshipReviewDTO) {
        if ((hardshipReviewDTO.getReviewStatus() != null &&
                hardshipReviewDTO.getReviewStatus().getStatus().equalsIgnoreCase(HardshipReviewStatus.COMPLETE.getStatus()))
                && (hardshipReviewDTO.getReviewDate() == null))
            throw new ValidationException(MSG_INVALID_REVIEW_DATE);

        return Optional.empty();
    }

}



