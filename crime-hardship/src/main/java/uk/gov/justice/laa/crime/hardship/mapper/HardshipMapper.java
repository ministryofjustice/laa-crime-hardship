package uk.gov.justice.laa.crime.hardship.mapper;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.hardship.dto.HardshipResult;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewDTO;
import uk.gov.justice.laa.crime.hardship.model.ApiPerformHardshipRequest;
import uk.gov.justice.laa.crime.hardship.model.ApiPerformHardshipResponse;
import uk.gov.justice.laa.crime.hardship.model.HardshipMetadata;
import uk.gov.justice.laa.crime.hardship.model.HardshipReview;

@Component
public class HardshipMapper implements RequestMapper<ApiPerformHardshipResponse, HardshipReviewDTO>,
        ResponseMapper<ApiPerformHardshipRequest, HardshipReviewDTO> {

    public ApiPerformHardshipResponse fromDto(HardshipReviewDTO reviewDTO) {

        HardshipReview hardship = reviewDTO.getHardship();
        HardshipResult hardshipResult = reviewDTO.getHardshipResult();

        return new ApiPerformHardshipResponse()
                .withReviewResult(hardshipResult.getResult())
                .withDisposableIncome(hardship.getTotalAnnualDisposableIncome())
                .withHardshipReviewId(reviewDTO.getHardshipMetadata().getHardshipReviewId())
                .withPostHardshipDisposableIncome(hardshipResult.getPostHardshipDisposableIncome());
    }

    public void toDto(ApiPerformHardshipRequest request, HardshipReviewDTO reviewDTO) {
        reviewDTO.setHardship(request.getHardship());
        reviewDTO.setHardshipMetadata(request.getHardshipMetadata());
    }
}
