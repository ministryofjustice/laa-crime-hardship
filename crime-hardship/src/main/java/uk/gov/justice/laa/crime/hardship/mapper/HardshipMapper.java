package uk.gov.justice.laa.crime.hardship.mapper;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.hardship.dto.HardshipResult;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewDTO;
import uk.gov.justice.laa.crime.hardship.model.ApiPerformHardshipResponse;
import uk.gov.justice.laa.crime.hardship.model.HardshipReview;

@Component
public class HardshipMapper implements RequestMapper<ApiPerformHardshipResponse, HardshipReviewDTO>,
        ResponseMapper<HardshipReview, HardshipReviewDTO> {

    public ApiPerformHardshipResponse fromDto(HardshipReviewDTO reviewDTO) {

        HardshipReview hardship = reviewDTO.getHardship();
        HardshipResult hardshipResult = reviewDTO.getHardshipResult();

        return new ApiPerformHardshipResponse()
                .withHardshipReviewId(hardship.getHardshipReviewId())
                .withReviewResult(hardshipResult.getResult())
                .withDisposableIncome(hardship.getTotalAnnualDisposableIncome())
                .withPostHardshipDisposableIncome(hardshipResult.getPostHardshipDisposableIncome());
    }

    public void toDto(HardshipReview hardship, HardshipReviewDTO reviewDTO) {
        reviewDTO.setHardship(hardship);
    }
}
