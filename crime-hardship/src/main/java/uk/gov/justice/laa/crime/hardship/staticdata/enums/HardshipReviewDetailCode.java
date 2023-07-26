package uk.gov.justice.laa.crime.hardship.staticdata.enums;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum HardshipReviewDetailCode {
    UNSECURED_LOAN("UNSECURED LOAN", "Unsecured Loan", HardshipReviewDetailType.EXPENDITURE.getType()),
    SECURED_LOAN("SECURED LOAN", "Secured Loan", HardshipReviewDetailType.EXPENDITURE.getType()),
    CAR_LOAN("CAR LOAN", "Car Loan", HardshipReviewDetailType.EXPENDITURE.getType()),
    IVA("IVA", "IVA", HardshipReviewDetailType.EXPENDITURE.getType()),
    CARDS("CARDS", "Credit/Store Card Payment", HardshipReviewDetailType.EXPENDITURE.getType()),
    DEBTS("DEBTS", "Debts", HardshipReviewDetailType.EXPENDITURE.getType()),
    FINES("FINES", "Fines", HardshipReviewDetailType.EXPENDITURE.getType()),
    RENT_ARREARS("RENT ARREARS", "Rent Arrears", HardshipReviewDetailType.EXPENDITURE.getType()),
    BAILIFF("BAILIFF", "Bailiff Costs", HardshipReviewDetailType.EXPENDITURE.getType()),
    DWP_OVERPAYMENT("DWP OVERPAYMENT", "DWP Overpayment", HardshipReviewDetailType.EXPENDITURE.getType()),
    STUDENT_LOAN("STUDENT LOAN", "Student Loan", HardshipReviewDetailType.EXPENDITURE.getType()),
    ADD_MORTGAGE("ADD MORTGAGE", "Mortgage on additional Property", HardshipReviewDetailType.EXPENDITURE.getType()),
    UNI_HOUSING("UNI HOUSING", "University Housing Costs", HardshipReviewDetailType.EXPENDITURE.getType()),
    PRESCRIPTION("PRESCRIPTION", "Prescription Costs", HardshipReviewDetailType.EXPENDITURE.getType()),
    PENSION_PAY("PENSION PAY", "Pension Payments", HardshipReviewDetailType.EXPENDITURE.getType()),
    MEDICAL_COSTS("MEDICAL COSTS", "Medical Costs", HardshipReviewDetailType.EXPENDITURE.getType()),
    OTHER("OTHER", "Other", HardshipReviewDetailType.EXPENDITURE.getType()),
    MEDICAL_GROUNDS("MEDICAL GROUNDS", "Medical Grounds", HardshipReviewDetailType.INCOME.getType()),
    SUSPENDED_WORK("SUSPENDED WORK", "Suspended from work", HardshipReviewDetailType.INCOME.getType()),
    OTHER_INC("OTHER INC", "Other", HardshipReviewDetailType.INCOME.getType());

    @JsonPropertyDescription("Hardship review detail codes that are valid")
    private final String code;
    private final String description;
    private final String type;

    public static HardshipReviewDetailCode getFrom(String code) {
        if (StringUtils.isBlank(code)) { return null; }

        List<HardshipReviewDetailCode> hardshipReviewDetailCodes =  Stream.of(HardshipReviewDetailCode.values())
                .filter(hrdCode -> hrdCode.code.equals(code))
                .toList();

        if (hardshipReviewDetailCodes.isEmpty()) {
            throw new IllegalArgumentException(String.format(
                    "Hardship review detail with code: %s does not exist.", code));
        } else if (hardshipReviewDetailCodes.size() > 1) {
            throw new IllegalArgumentException(String.format(
                    "Hardship review detail code: %s returned non unique value", code));
        } else {
            return hardshipReviewDetailCodes.get(0);
        }

    }

}
