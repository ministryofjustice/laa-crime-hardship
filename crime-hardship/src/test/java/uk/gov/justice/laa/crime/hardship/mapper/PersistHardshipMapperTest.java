package uk.gov.justice.laa.crime.hardship.mapper;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.laa.crime.hardship.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.hardship.dto.HardshipResult;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewDTO;
import uk.gov.justice.laa.crime.hardship.model.*;
import uk.gov.justice.laa.crime.hardship.model.maat_api.*;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewDetailCode;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewDetailType;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.RequestType;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SoftAssertionsExtension.class)
class PersistHardshipMapperTest {


    HardshipReviewDTO reviewDTO;
    PersistHardshipMapper mapper = new PersistHardshipMapper();

    @InjectSoftAssertions
    private SoftAssertions softly;

    @BeforeEach
    void setup() {
        this.reviewDTO = HardshipReviewDTO.builder()
                .hardshipResult(TestModelDataBuilder.getHardshipResult())
                .hardship(TestModelDataBuilder.getMinimalHardshipReview())
                .hardshipMetadata(TestModelDataBuilder.getHardshipMetadata())
                .build();
    }

    @Test
    void givenMinimalHardshipReviewDTO_whenFromDtoIsInvoked_thenRequestIsMapped() {

        HardshipReview hardship = reviewDTO.getHardship();
        HardshipResult result = reviewDTO.getHardshipResult();
        HardshipMetadata metadata = reviewDTO.getHardshipMetadata();

        ApiPersistHardshipRequest request = mapper.fromDto(reviewDTO);

        softly.assertThat(request.getNworCode())
                .isEqualTo(metadata.getReviewReason().getCode());
        softly.assertThat(request.getCmuId())
                .isEqualTo(metadata.getCmuId());
        softly.assertThat(request.getReviewResult())
                .isEqualTo(result.getResult());
        softly.assertThat(request.getReviewDate())
                .isEqualTo(hardship.getReviewDate());
        softly.assertThat(request.getResultDate().toLocalDate())
                .isEqualTo(LocalDateTime.now().toLocalDate());
        softly.assertThat(request.getNotes())
                .isEqualTo(metadata.getNotes());
        softly.assertThat(request.getDecisionNotes())
                .isEqualTo(metadata.getDecisionNotes());
        softly.assertThat(request.getStatus())
                .isEqualTo(metadata.getReviewStatus());
        softly.assertThat(request.getDisposableIncome())
                .isEqualTo(hardship.getTotalAnnualDisposableIncome());
        softly.assertThat(request.getDisposableIncomeAfterHardship())
                .isEqualTo(result.getPostHardshipDisposableIncome());

        var solicitorsCosts = request.getSolicitorCosts();
        softly.assertThat(solicitorsCosts.getSolicitorDisb())
                .isEqualTo(hardship.getSolicitorCosts().getDisbursements());
        softly.assertThat(solicitorsCosts.getSolicitorRate())
                .isEqualTo(hardship.getSolicitorCosts().getRate());
        softly.assertThat(solicitorsCosts.getSolicitorVat())
                .isEqualTo(hardship.getSolicitorCosts().getVat());
        softly.assertThat(solicitorsCosts.getSolicitorHours())
                .isEqualTo(hardship.getSolicitorCosts().getHours());
        softly.assertThat(solicitorsCosts.getSolicitorEstTotalCost())
                .isEqualTo(hardship.getSolicitorCosts().getEstimatedTotal());
    }

    @Test
    void givenHardshipReviewDTOWithDetails_whenFromDtoIsInvoked_thenRequestIsMapped() {
        HardshipReview hardship = reviewDTO.getHardship();
        HardshipMetadata metadata = reviewDTO.getHardshipMetadata();

        DeniedIncome deniedIncome = TestModelDataBuilder.getDeniedIncome();
        ExtraExpenditure extraExpenditure = TestModelDataBuilder.getExtraExpenditure();
        OtherFundingSource otherFundingSource = TestModelDataBuilder.getOtherFundingSources();

        hardship.setDeniedIncome(List.of(deniedIncome));
        hardship.setExtraExpenditure(List.of(extraExpenditure));
        hardship.setOtherFundingSources(List.of(otherFundingSource));

        ApiPersistHardshipRequest request = mapper.fromDto(reviewDTO);
        List<ApiHardshipDetail> reviewDetails = request.getReviewDetails();

        assertThat(reviewDetails)
                .asList()
                .hasSize(3);

        List<ApiHardshipDetail> expected = List.of(

                new ApiHardshipDetail()
                        .withAccepted(true)
                        .withType(HardshipReviewDetailType.INCOME)
                        .withAmount(deniedIncome.getAmount())
                        .withFrequency(deniedIncome.getFrequency())
                        .withUserCreated(metadata.getUserSession().getUserName())
                        .withDetailCode(HardshipReviewDetailCode.MEDICAL_GROUNDS),

                new ApiHardshipDetail()
                        .withAccepted(true)
                        .withType(HardshipReviewDetailType.EXPENDITURE)
                        .withAmount(extraExpenditure.getAmount())
                        .withFrequency(extraExpenditure.getFrequency())
                        .withUserCreated(metadata.getUserSession().getUserName())
                        .withDetailCode(HardshipReviewDetailCode.CARDS)
                        .withDetailReason(extraExpenditure.getReasonCode()),

                new ApiHardshipDetail()
                        .withType(HardshipReviewDetailType.FUNDING)
                        .withAmount(otherFundingSource.getAmount())
                        .withDateDue(otherFundingSource.getDueDate())
                        .withUserCreated(metadata.getUserSession().getUserName())
                        .withOtherDescription(otherFundingSource.getDescription())
        );

        softly.assertThat(reviewDetails)
                .usingRecursiveFieldByFieldElementComparator()
                .containsAll(expected);
    }

    @Test
    void givenHardshipReviewDTOWithProgress_whenFromDtoIsInvoked_thenRequestIsMapped() {
        HardshipMetadata metadata = reviewDTO.getHardshipMetadata();
        HardshipProgress progress = TestModelDataBuilder.getHardshipProgress();
        metadata.setProgressItems(List.of(progress));

        ApiPersistHardshipRequest request = mapper.fromDto(reviewDTO);

        List<ApiHardshipProgress> expected = List.of(
                new ApiHardshipProgress()
                        .withProgressAction(progress.getAction())
                        .withProgressResponse(progress.getResponse())
                        .withDateRequested(progress.getDateTaken())
                        .withDateCompleted(progress.getDateCompleted())
                        .withDateRequired(progress.getDateRequired())
                        .withUserCreated(metadata.getUserSession().getUserName())
        );

        assertThat(request.getReviewProgressItems())
                .asList()
                .usingRecursiveFieldByFieldElementComparator()
                .containsAll(expected);

    }

    @Test
    void givenHardshipReviewDTOAndCreateRequest_whenFromDtoIsInvoked_thenRequestIsMapped() {
        reviewDTO.setRequestType(RequestType.CREATE);
        HardshipReview hardship = reviewDTO.getHardship();
        HardshipMetadata metadata = reviewDTO.getHardshipMetadata();

        ApiCreateHardshipRequest request = (ApiCreateHardshipRequest) mapper.fromDto(reviewDTO);

        softly.assertThat(request.getRepId())
                .isEqualTo(metadata.getRepId());
        softly.assertThat(request.getUserCreated())
                .isEqualTo(metadata.getUserSession().getUserName());
        softly.assertThat(request.getCourtType())
                .isEqualTo(hardship.getCourtType());
        softly.assertThat(request.getFinancialAssessmentId())
                .isEqualTo(metadata.getFinancialAssessmentId());
    }

    @Test
    void givenHardshipReviewDTOAndUpdateRequest_whenFromDtoIsInvoked_thenRequestIsMapped() {
        reviewDTO.setRequestType(RequestType.UPDATE);
        HardshipMetadata metadata = reviewDTO.getHardshipMetadata();

        ApiUpdateHardshipRequest request = (ApiUpdateHardshipRequest) mapper.fromDto(reviewDTO);

        softly.assertThat(request.getId())
                .isEqualTo(metadata.getHardshipReviewId());
        softly.assertThat(request.getUserModified())
                .isEqualTo(metadata.getUserSession().getUserName());
    }

    @Test
    void givenMinimalHardshipReviewDTOAndNoResult_whenFromDtoIsInvoked_thenRequestIsMapped() {
        reviewDTO.setHardshipResult(null);
        ApiPersistHardshipRequest request = mapper.fromDto(reviewDTO);

        softly.assertThat(request.getResultDate())
                .isNull();
        softly.assertThat(request.getReviewResult())
                .isNull();
        softly.assertThat(request.getDisposableIncomeAfterHardship())
                .isNull();
    }

    @Test
    void givenApiPersistHardshipResponse_whenToDtoIsInvoked_thenDtoIsMapped() {
        ApiPersistHardshipResponse response = new ApiPersistHardshipResponse()
                .withId(TestModelDataBuilder.HARDSHIP_ID);

        mapper.toDto(response, reviewDTO);

        assertThat(reviewDTO.getHardshipMetadata().getHardshipReviewId())
                .isEqualTo(response.getId());
    }
}