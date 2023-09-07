package uk.gov.justice.laa.crime.hardship.mapper;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.hardship.dto.HardshipResult;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewDTO;
import uk.gov.justice.laa.crime.hardship.dto.maat_api.SolicitorCosts;
import uk.gov.justice.laa.crime.hardship.model.*;
import uk.gov.justice.laa.crime.hardship.model.maat_api.*;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.Frequency;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewDetailCode;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewDetailType;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.RequestType;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Component
public class PersistHardshipMapper implements RequestMapper<ApiPersistHardshipRequest, HardshipReviewDTO>,
        ResponseMapper<ApiPersistHardshipResponse, HardshipReviewDTO> {

    public ApiPersistHardshipRequest fromDto(HardshipReviewDTO reviewDTO) {

        ApiPersistHardshipRequest request;
        HardshipReview hardship = reviewDTO.getHardship();
        HardshipMetadata metadata = reviewDTO.getHardshipMetadata();
        HardshipResult hardshipResult = reviewDTO.getHardshipResult();

        if (RequestType.CREATE == reviewDTO.getRequestType()) {
            request = new ApiCreateHardshipRequest()
                    .withRepId(metadata.getRepId())
                    .withUserCreated(metadata.getUserSession().getUserName())
                    .withCourtType(hardship.getCourtType())
                    .withFinancialAssessmentId(metadata.getFinancialAssessmentId());
        } else {
            request = new ApiUpdateHardshipRequest()
                    .withId(metadata.getHardshipReviewId())
                    .withUserModified(metadata.getUserSession().getUserName());
        }

        boolean isResult = hardshipResult != null;

        return request
                .withNworCode(metadata.getReviewReason().getCode())
                .withCmuId(metadata.getCmuId())
                .withReviewResult(
                        isResult ? hardshipResult.getResult() : null
                )
                .withReviewDate(hardship.getReviewDate())
                .withResultDate(
                        isResult ? LocalDateTime.now() : null
                )
                .withNotes(metadata.getNotes())
                .withDecisionNotes(metadata.getDecisionNotes())
                .withSolicitorCosts(
                        SolicitorCosts.builder()
                                .solicitorRate(hardship.getSolicitorCosts().getRate())
                                .solicitorDisb(hardship.getSolicitorCosts().getDisbursements())
                                .solicitorVat(hardship.getSolicitorCosts().getVat())
                                .solicitorEstTotalCost(hardship.getSolicitorCosts().getEstimatedTotal())
                                .solicitorHours(hardship.getSolicitorCosts().getHours())
                                .build()
                )
                .withStatus(metadata.getReviewStatus())
                .withDisposableIncome(hardship.getTotalAnnualDisposableIncome())
                .withDisposableIncomeAfterHardship(
                        isResult ? hardshipResult.getPostHardshipDisposableIncome() : null
                )
                .withReviewProgressItems(convertHardshipProgress(metadata))
                .withReviewDetails(convertHardshipDetails(hardship, metadata.getUserSession().getUserName()));
    }

    private List<ApiHardshipDetail> convertHardshipDetails(HardshipReview hardship, String username) {

        return Stream.concat(
                Stream.of(hardship.getDeniedIncome(), hardship.getExtraExpenditure(),
                                hardship.getOtherFundingSources()
                        )
                        .flatMap(Collection::stream)
                        .map(item -> {
                            var detail = new ApiHardshipDetail()
                                    .withAmount(item.getAmount())
                                    .withOtherDescription(item.getDescription())
                                    .withUserCreated(username);
                            if (item instanceof OtherFundingSource otherFunding) {
                                return detail
                                        .withType(HardshipReviewDetailType.FUNDING)
                                        .withDateDue(otherFunding.getDueDate());
                            } else if (item instanceof HardshipCost hardshipCost) {
                                detail
                                        .withFrequency(hardshipCost.getFrequency())
                                        .withAccepted(Boolean.TRUE.equals(hardshipCost.getAccepted()) ? "Y" : "N");

                                if (item instanceof DeniedIncome deniedIncome) {
                                    return detail
                                            .withType(HardshipReviewDetailType.INCOME)
                                            .withDetailCode(
                                                    HardshipReviewDetailCode.getFrom(
                                                            deniedIncome.getItemCode().getCode()
                                                    )
                                            );
                                } else if (item instanceof ExtraExpenditure expenditure) {
                                    return detail
                                            .withType(HardshipReviewDetailType.EXPENDITURE)
                                            .withDetailCode(HardshipReviewDetailCode.getFrom(
                                                            expenditure.getItemCode().getCode()
                                                    )
                                            )
                                            .withDetailReason(expenditure.getReasonCode());
                                }
                            }
                            return detail;
                        }),

                Stream.of(
                        new ApiHardshipDetail()
                                .withType(HardshipReviewDetailType.SOL_COSTS)
                                .withAmount(hardship.getSolicitorCosts().getEstimatedTotal())
                                .withFrequency(Frequency.ANNUALLY)
                                .withAccepted("Y")
                )
        ).toList();
    }

    private List<ApiHardshipProgress> convertHardshipProgress(HardshipMetadata metadata) {
        return metadata.getProgressItems().stream()
                .map(item -> new ApiHardshipProgress()
                        .withDateRequested(item.getDateTaken())
                        .withDateRequired(item.getDateRequired())
                        .withDateCompleted(item.getDateCompleted())
                        .withProgressAction(item.getAction())
                        .withProgressResponse(item.getResponse())
                        .withUserCreated(metadata.getUserSession().getUserName())
                ).toList();
    }

    public void toDto(ApiPersistHardshipResponse response, HardshipReviewDTO reviewDTO) {
        reviewDTO.getHardshipMetadata().setHardshipReviewId(response.getId());
    }
}
