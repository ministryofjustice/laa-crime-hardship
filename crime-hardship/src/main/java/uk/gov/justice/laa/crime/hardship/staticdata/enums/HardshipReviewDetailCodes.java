package uk.gov.justice.laa.crime.hardship.staticdata.enums;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum HardshipReviewDetailCodes {
    UNSECURED_LOAN("UNSECURED LOAN", "Unsecured Loan", HardshipReviewDetailType.EXPENDITURE),
    SECURED_LOAN("SECURED LOAN", "Secured Loan", HardshipReviewDetailType.EXPENDITURE),
    CAR_LOAN("CAR LOAN", "Car Loan", HardshipReviewDetailType.EXPENDITURE),
    IVA("IVA", "IVA", HardshipReviewDetailType.EXPENDITURE),
    CARDS("CARDS", "Credit/Store Card Payment", HardshipReviewDetailType.EXPENDITURE),
    DEBTS("DEBTS", "Debts", HardshipReviewDetailType.EXPENDITURE),
    FINES("FINES", "Fines", HardshipReviewDetailType.EXPENDITURE),
    RENT_ARREARS("RENT ARREARS", "Rent Arrears", HardshipReviewDetailType.EXPENDITURE),
    BAILIFF("BAILIFF", "Bailiff Costs", HardshipReviewDetailType.EXPENDITURE),
    DWP_OVERPAYMENT("DWP OVERPAYMENT", "DWP Overpayment", HardshipReviewDetailType.EXPENDITURE),
    STUDENT_LOAN("STUDENT LOAN", "Student Loan", HardshipReviewDetailType.EXPENDITURE),
    ADD_MORTGAGE("ADD MORTGAGE", "Mortgage on additional Property", HardshipReviewDetailType.EXPENDITURE),
    UNI_HOUSING("UNI HOUSING", "University Housing Costs", HardshipReviewDetailType.EXPENDITURE),
    PRESCRIPTION("PRESCRIPTION", "Prescription Costs", HardshipReviewDetailType.EXPENDITURE),
    PENSION_PAY("PENSION PAY", "Pension Payments", HardshipReviewDetailType.EXPENDITURE),
    MEDICAL_COSTS("MEDICAL COSTS", "Medical Costs", HardshipReviewDetailType.EXPENDITURE),
    OTHER("OTHER", "Other", HardshipReviewDetailType.EXPENDITURE),
    MEDICAL_GROUNDS("MEDICAL GROUNDS", "Medical Grounds", HardshipReviewDetailType.INCOME),
    SUSPENDED_WORK("SUSPENDED WORK", "Suspended from work", HardshipReviewDetailType.INCOME),
    OTHER_INC("OTHER INC", "Other", HardshipReviewDetailType.INCOME);

    @JsonPropertyDescription("Hardship review detail codes that are valid")
    private String code;
    private String description;
    private HardshipReviewDetailType type;

    public static HardshipReviewDetailCodes getFrom(String code) {
        if (StringUtils.isBlank(code)) { return null; }

        return Stream.of(HardshipReviewDetailCodes.values())
                .filter(hrdCode -> hrdCode.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "Hardship review detail with code: %s does not exist.", code)));

    }

}
