package uk.gov.justice.laa.crime.hardship.staticdata.enums;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum HardshipReviewDetailReasons {


    EVIDENCE_SUPPLIED("Evidence Supplied", HardshipReviewDetailType.EXPENDITURE.getType()),
    ESSENTIAL_NEED_FOR_WORK("Essential - need for work", HardshipReviewDetailType.EXPENDITURE.getType()),
    ESSENTIAL_ITEM("Essential Item", HardshipReviewDetailType.EXPENDITURE.getType()),
    ARRANGEMENT_IN_PLACE("Arrangement in place", HardshipReviewDetailType.EXPENDITURE.getType()),
    NO_EVIDENCE_SUPPLIED("No evidence supplied", HardshipReviewDetailType.EXPENDITURE.getType()),
    INSUFFICIENT_EVIDENCE_SUPPLIED("Insufficient evidence supplied", HardshipReviewDetailType.EXPENDITURE.getType()),
    NON_ESSENTIAL_ITEM_EXPENSE("Non-essential item/expense", HardshipReviewDetailType.EXPENDITURE.getType()),
    COVERED_BY_LIVING_EXPENSE("Covered by living expense", HardshipReviewDetailType.EXPENDITURE.getType()),
    NOT_ALLOWABLE_DIFF_FROM_NON_ESSENTIAL("Not allowable (diff from non-essential)", HardshipReviewDetailType.EXPENDITURE.getType()),
    NOT_IN_COMPUTATION_PERIOD("Not in computation period", HardshipReviewDetailType.EXPENDITURE.getType());

    @JsonPropertyDescription("Hardship review detail reasons")
    private final String reason;
    private final String type;

    public static HardshipReviewDetailReasons getFrom(String reason) {
        if (StringUtils.isBlank(reason)) { return null; }

        List<HardshipReviewDetailReasons> extraExpenditureDetailCodes =  Stream.of(HardshipReviewDetailReasons.values())
                .filter(eedCode -> eedCode.reason.equals(reason))
                .toList();

        if (extraExpenditureDetailCodes.isEmpty()) {
            throw new IllegalArgumentException(String.format(
                    "Hardship review detail reason: %s does not exist.", reason));
        } else if (extraExpenditureDetailCodes.size() > 1) {
            throw new IllegalArgumentException(String.format(
                    "Hardship review detail reason: %s returned non unique value", reason));
        } else {
            return extraExpenditureDetailCodes.get(0);
        }

    }

}
