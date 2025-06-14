package uk.gov.justice.laa.crime.hardship.mapper;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.laa.crime.common.model.hardship.*;
import uk.gov.justice.laa.crime.common.model.hardship.maat_api.*;
import uk.gov.justice.laa.crime.enums.Frequency;
import uk.gov.justice.laa.crime.enums.HardshipReviewDetailCode;
import uk.gov.justice.laa.crime.enums.HardshipReviewResult;
import uk.gov.justice.laa.crime.enums.RequestType;
import uk.gov.justice.laa.crime.hardship.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.hardship.dto.HardshipResult;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewDTO;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static uk.gov.justice.laa.crime.enums.HardshipReviewDetailType.*;

@ExtendWith(SoftAssertionsExtension.class)
class PersistHardshipMapperTest {


    HardshipReviewDTO reviewDTO;
    PersistHardshipMapper mapper = new PersistHardshipMapper();

    @InjectSoftAssertions
    private SoftAssertions softly;

    @BeforeEach
    void setup() {
        this.reviewDTO = HardshipReviewDTO.builder()
                .hardship(TestModelDataBuilder.getMinimalHardshipReview())
                .hardshipMetadata(TestModelDataBuilder.getHardshipMetadata())
                .hardshipResult(TestModelDataBuilder.getHardshipResult(HardshipReviewResult.PASS))
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
        softly.assertThat(solicitorsCosts.getDisbursements())
                .isEqualTo(hardship.getSolicitorCosts().getDisbursements());
        softly.assertThat(solicitorsCosts.getRate())
                .isEqualTo(hardship.getSolicitorCosts().getRate());
        softly.assertThat(solicitorsCosts.getVat())
                .isEqualTo(hardship.getSolicitorCosts().getVat());
        softly.assertThat(solicitorsCosts.getHours())
                .isEqualTo(hardship.getSolicitorCosts().getHours());
        softly.assertThat(solicitorsCosts.getEstimatedTotal())
                .isEqualTo(hardship.getSolicitorCosts().getEstimatedTotal());
    }

    @Test
    void givenHardshipReviewDTOWithDetails_whenFromDtoIsInvoked_thenRequestIsMapped() {
        HardshipReview hardship = reviewDTO.getHardship();
        HardshipMetadata metadata = reviewDTO.getHardshipMetadata();

        DeniedIncome deniedIncome = TestModelDataBuilder.getDeniedIncome();
        ExtraExpenditure extraExpenditure = TestModelDataBuilder.getExtraExpenditure();

        hardship.setDeniedIncome(List.of(deniedIncome));
        hardship.setExtraExpenditure(List.of(extraExpenditure));
        hardship.getExtraExpenditure().get(0).setAccepted(false);

        ApiPersistHardshipRequest request = mapper.fromDto(reviewDTO);
        List<ApiHardshipDetail> reviewDetails = request.getReviewDetails();

        assertThat(reviewDetails)
                .asInstanceOf(InstanceOfAssertFactories.LIST)
                .hasSize(3);

        List<ApiHardshipDetail> expected = List.of(

                new ApiHardshipDetail()
                        .withAccepted("Y")
                        .withDetailType(INCOME)
                        .withAmount(deniedIncome.getAmount())
                        .withFrequency(deniedIncome.getFrequency())
                        .withReasonNote("Hospitalisation")
                        .withUserCreated(metadata.getUserSession().getUserName())
                        .withDetailCode(HardshipReviewDetailCode.MEDICAL_GROUNDS),

                new ApiHardshipDetail()
                        .withAccepted("N")
                        .withDetailType(EXPENDITURE)
                        .withAmount(extraExpenditure.getAmount())
                        .withFrequency(extraExpenditure.getFrequency())
                        .withUserCreated(metadata.getUserSession().getUserName())
                        .withDetailCode(HardshipReviewDetailCode.CARDS)
                        .withDetailReason(extraExpenditure.getReasonCode()),

                new ApiHardshipDetail()
                        .withDetailType(SOL_COSTS)
                        .withAmount(hardship.getSolicitorCosts().getEstimatedTotal())
                        .withFrequency(Frequency.ANNUALLY)
                        .withAccepted("Y")
        );

        assertThat(reviewDetails)
                .asInstanceOf(InstanceOfAssertFactories.LIST)
                .usingRecursiveFieldByFieldElementComparator()
                .containsAll(expected);
    }
    @Test
    void givenHardshipReviewDTOWithoutSolicitorCosts_whenFromDtoIsInvoked_thenRequestIsMapped() {
        HardshipReview hardship = reviewDTO.getHardship();
        hardship.setSolicitorCosts(null);
        ApiPersistHardshipRequest request = mapper.fromDto(reviewDTO);
        assertThat(request.getSolicitorCosts()).isNull();
        assertThat(request.getReviewDetails().stream().filter(detail -> detail.getDetailType() == SOL_COSTS).findFirst()).isEmpty();
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
