package uk.gov.justice.laa.crime.hardship.mapper;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.laa.crime.common.model.hardship.ApiPerformHardshipRequest;
import uk.gov.justice.laa.crime.common.model.hardship.ApiPerformHardshipResponse;
import uk.gov.justice.laa.crime.common.model.hardship.HardshipMetadata;
import uk.gov.justice.laa.crime.common.model.hardship.HardshipReview;
import uk.gov.justice.laa.crime.enums.HardshipReviewResult;
import uk.gov.justice.laa.crime.hardship.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.hardship.dto.HardshipResult;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewDTO;

@ExtendWith(SoftAssertionsExtension.class)
class HardshipMapperTest {

    HardshipMapper mapper = new HardshipMapper();

    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void givenHardshipReviewDTO_whenFromDtoIsInvoked_thenResponseIsMapped() {

        HardshipMetadata metadata = TestModelDataBuilder.getHardshipMetadata();
        HardshipReview hardship = TestModelDataBuilder.getMinimalHardshipReview();
        HardshipResult result = TestModelDataBuilder.getHardshipResult(HardshipReviewResult.PASS);

        HardshipReviewDTO reviewDTO = HardshipReviewDTO.builder()
                .hardship(hardship)
                .hardshipResult(result)
                .hardshipMetadata(metadata)
                .build();

        ApiPerformHardshipResponse response = mapper.fromDto(reviewDTO);

        softly.assertThat(response.getHardshipReviewId())
                .isEqualTo(metadata.getHardshipReviewId());

        softly.assertThat(response.getDisposableIncome())
                .isEqualTo(hardship.getTotalAnnualDisposableIncome());

        softly.assertThat(response.getPostHardshipDisposableIncome())
                .isEqualTo(result.getPostHardshipDisposableIncome());

        softly.assertThat(response.getReviewResult())
                .isEqualTo(result.getResult());
    }

    @Test
    void givenApiPerformHardshipRequest_whenToDtoIsInvoked_thenDtoIsMapped() {
        ApiPerformHardshipRequest hardship = new ApiPerformHardshipRequest()
                .withHardshipMetadata(
                        new HardshipMetadata()
                                .withHardshipReviewId(TestModelDataBuilder.HARDSHIP_ID)
                );
        HardshipReviewDTO reviewDTO = new HardshipReviewDTO();

        mapper.toDto(hardship, reviewDTO);

        softly.assertThat(reviewDTO.getHardship())
                .isEqualTo(hardship.getHardship());
        softly.assertThat(reviewDTO.getHardshipMetadata())
                .isEqualTo(hardship.getHardshipMetadata());
    }
}
