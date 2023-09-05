package uk.gov.justice.laa.crime.hardship.converter;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.hardship.dto.HardshipResult;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewDTO;
import uk.gov.justice.laa.crime.hardship.model.ApiHardshipResponse;
import uk.gov.justice.laa.crime.hardship.model.HardshipReview;

@Component
public class HardshipMapper implements RequestMapper<ApiHardshipResponse, HardshipReviewDTO>,
        ResponseMapper<HardshipReview, HardshipReviewDTO> {

    public ApiHardshipResponse fromDto(HardshipReviewDTO reviewDTO) {

        HardshipReview hardship = reviewDTO.getHardship();
        HardshipResult hardshipResult = reviewDTO.getHardshipResult();

        return new ApiHardshipResponse()
                .withHardshipReviewId(hardship.getHardshipReviewId())
                .withReviewResult(hardshipResult.getResult())
                .withDisposableIncome(hardship.getTotalAnnualDisposableIncome())
                .withPostHardshipDisposableIncome(hardshipResult.getPostHardshipDisposableIncome());
    }

    @Override
    public void toDto(HardshipReview hardship, HardshipReviewDTO reviewDTO) {
        reviewDTO.setHardship(hardship);
    }
}
