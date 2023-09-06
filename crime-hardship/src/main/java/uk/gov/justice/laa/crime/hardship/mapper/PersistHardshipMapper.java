package uk.gov.justice.laa.crime.hardship.mapper;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.hardship.dto.HardshipResult;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewDTO;
import uk.gov.justice.laa.crime.hardship.dto.maat_api.SolicitorCosts;
import uk.gov.justice.laa.crime.hardship.model.*;
import uk.gov.justice.laa.crime.hardship.model.maat_api.*;
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
        HardshipResult hardshipResult = reviewDTO.getHardshipResult();

        if (RequestType.CREATE == reviewDTO.getRequestType()) {
            request = new ApiCreateHardshipRequest()
                    .withRepId(hardship.getRepId())
                    .withUserCreated(hardship.getUserSession().getUserName())
                    .withCourtType(hardship.getCourtType())
                    .withFinancialAssessmentId(hardship.getFinancialAssessmentId());
        } else {
            request = new ApiUpdateHardshipRequest()
                    .withId(hardship.getHardshipReviewId())
                    .withUserModified(hardship.getUserSession().getUserName());
        }

        return request
                .withNworCode(hardship.getReviewReason().getCode())
                .withCmuId(hardship.getCmuId())
                .withReviewResult(hardshipResult.getResult())
                .withReviewDate(hardship.getReviewDate())
                .withResultDate(
                        hardshipResult.getResult() != null ? LocalDateTime.now() : null
                )
                .withNotes(hardship.getNotes())
                .withDecisionNotes(hardship.getDecisionNotes())
                .withSolicitorCosts(
                        SolicitorCosts.builder()
                                .solicitorRate(hardship.getSolicitorCosts().getRate())
                                .solicitorDisb(hardship.getSolicitorCosts().getDisbursements())
                                .solicitorVat(hardship.getSolicitorCosts().getVat())
                                .solicitorEstTotalCost(hardship.getSolicitorCosts().getEstimatedTotal())
                                .solicitorHours(hardship.getSolicitorCosts().getHours())
                                .build()
                )
                .withStatus(hardship.getReviewStatus())
                .withDisposableIncome(hardship.getTotalAnnualDisposableIncome())
                .withDisposableIncomeAfterHardship(hardshipResult.getPostHardshipDisposableIncome())
                .withReviewDetails(convertHardshipDetails(hardship))
                .withReviewProgressItems(convertHardshipProgress(hardship));
    }

    private List<ApiHardshipDetail> convertHardshipDetails(HardshipReview hardship) {
        return Stream.of(hardship.getDeniedIncome(), hardship.getExtraExpenditure(),
                        hardship.getOtherFundingSources()
                )
                .flatMap(Collection::stream)
                .map(item -> {
                    var detail = new ApiHardshipDetail()
                            .withAmount(item.getAmount())
                            .withOtherDescription(item.getDescription())
                            .withUserCreated(hardship.getUserSession().getUserName());
                    if (item instanceof OtherFundingSource otherFunding) {
                        return detail
                                .withType(HardshipReviewDetailType.FUNDING)
                                .withDateDue(otherFunding.getDueDate());
                    } else if (item instanceof HardshipCost hardshipCost) {
                        detail
                                .withFrequency(hardshipCost.getFrequency())
                                .withAccepted(hardshipCost.getAccepted());

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
                }).toList();
    }

    private List<ApiHardshipProgress> convertHardshipProgress(HardshipReview hardship) {
        return hardship.getProgressItems().stream()
                .map(item -> new ApiHardshipProgress()
                        .withDateRequested(item.getDateTaken())
                        .withDateRequired(item.getDateRequired())
                        .withDateCompleted(item.getDateCompleted())
                        .withProgressAction(item.getAction())
                        .withProgressResponse(item.getResponse())
                        .withUserCreated(hardship.getUserSession().getUserName())
                ).toList();
    }

    public void toDto(ApiPersistHardshipResponse response, HardshipReviewDTO reviewDTO) {
        reviewDTO.getHardship().setHardshipReviewId(response.getId());
    }
}
