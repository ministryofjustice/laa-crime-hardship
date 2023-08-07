package uk.gov.justice.laa.crime.hardship.data.builder;

import jakarta.servlet.http.PushBuilder;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewDetail;
import uk.gov.justice.laa.crime.hardship.model.ApiCalculateHardshipByDetailRequest;
import uk.gov.justice.laa.crime.hardship.model.ApiCalculateHardshipByDetailResponse;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.Frequency;

import java.math.BigDecimal;
import java.util.List;

@Component
public class TestModelDataBuilder {

    public static final Integer TEST_REP_ID = 91919;
    public static final String MEANS_ASSESSMENT_TRANSACTION_ID = "7c49ebfe-fe3a-4f2f-8dad-f7b8f03b8327";
    public static final String DETAIL_TYPE = "EXPENDITURE";
    public static final BigDecimal HARDSHIP_SUMMARY = BigDecimal.valueOf(100.12);
    public static final Integer HARDSHIP_ID = 1234;
    public static final BigDecimal HARDSHIP_AMOUNT = BigDecimal.valueOf(10.0);

    public static ApiCalculateHardshipByDetailRequest getApiCalculateHardshipByDetailRequest(boolean isValid) {
        return new ApiCalculateHardshipByDetailRequest()
                .withRepId(isValid ? TEST_REP_ID : null)
                .withLaaTransactionId(MEANS_ASSESSMENT_TRANSACTION_ID)
                .withDetailType(DETAIL_TYPE);
    }

    public static ApiCalculateHardshipByDetailResponse getApiCalculateHardshipByDetailResponse() {
        return new ApiCalculateHardshipByDetailResponse()
                .withHardshipSummary(HARDSHIP_SUMMARY);
    }

    public static List<HardshipReviewDetail> getHardshipReviewDetailList(String accepted, double amount) {
        return List.of(HardshipReviewDetail.builder()
                        .id(HARDSHIP_ID)
                        .accepted(accepted)
                        .amount(BigDecimal.valueOf(amount))
                        .frequency(Frequency.ANNUALLY)
                        .build());
    }
}