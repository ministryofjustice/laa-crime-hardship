package uk.gov.justice.laa.crime.hardship.mapper;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.common.model.hardship.ApiHardshipDetail;
import uk.gov.justice.laa.crime.common.model.hardship.DeniedIncome;
import uk.gov.justice.laa.crime.common.model.hardship.ExtraExpenditure;
import uk.gov.justice.laa.crime.common.model.hardship.HardshipMetadata;
import uk.gov.justice.laa.crime.common.model.hardship.HardshipReview;
import uk.gov.justice.laa.crime.common.model.hardship.maat_api.*;
import uk.gov.justice.laa.crime.dto.maatapi.SolicitorCosts;
import uk.gov.justice.laa.crime.enums.Frequency;
import uk.gov.justice.laa.crime.enums.HardshipReviewDetailCode;
import uk.gov.justice.laa.crime.enums.HardshipReviewDetailType;
import uk.gov.justice.laa.crime.enums.RequestType;
import uk.gov.justice.laa.crime.hardship.dto.HardshipResult;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewDTO;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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
                .withSolicitorCosts(Objects.isNull(hardship.getSolicitorCosts()) ? null :
                        SolicitorCosts.builder()
                                .rate(hardship.getSolicitorCosts().getRate())
                                .disbursements(hardship.getSolicitorCosts().getDisbursements())
                                .vat(hardship.getSolicitorCosts().getVat())
                                .estimatedTotal(hardship.getSolicitorCosts().getEstimatedTotal())
                                .hours(hardship.getSolicitorCosts().getHours())
                                .build()
                )
                .withStatus(metadata.getReviewStatus())
                .withDisposableIncome(hardship.getTotalAnnualDisposableIncome())
                .withDisposableIncomeAfterHardship(
                        isResult ? hardshipResult.getPostHardshipDisposableIncome() : null
                )
                .withReviewDetails(convertHardshipDetails(hardship, metadata.getUserSession().getUserName()));
    }

    private List<ApiHardshipDetail> convertHardshipDetails(HardshipReview hardship, String username) {

        List<ApiHardshipDetail> apiHardshipDetails = Stream.of(hardship.getDeniedIncome(), hardship.getExtraExpenditure())
                .flatMap(Collection::stream)
                .map(item -> {
                            ApiHardshipDetail detail = new ApiHardshipDetail()
                                    .withAmount(item.getAmount())
                                    .withOtherDescription(item.getDescription())
                                    .withUserCreated(username)
                                    .withFrequency(item.getFrequency())
                                    .withAccepted(Boolean.TRUE.equals(item.getAccepted()) ? "Y" : "N");

                            if (item instanceof DeniedIncome deniedIncome) {
                                return detail
                                        .withDetailType(HardshipReviewDetailType.INCOME)
                                        .withReasonNote(deniedIncome.getReasonNote())
                                        .withDetailCode(
                                                HardshipReviewDetailCode.getFrom(
                                                        deniedIncome.getItemCode().getCode()
                                                )
                                        );
                            } else if (item instanceof ExtraExpenditure expenditure) {
                                return detail
                                        .withDetailType(HardshipReviewDetailType.EXPENDITURE)
                                        .withDetailCode(HardshipReviewDetailCode.getFrom(
                                                        expenditure.getItemCode().getCode()
                                                )
                                        )
                                        .withDetailReason(expenditure.getReasonCode());
                            }
                            return detail;
                        }
                ).collect(Collectors.toList());
        if (Objects.nonNull(hardship.getSolicitorCosts())) {
            apiHardshipDetails.add(new ApiHardshipDetail()
                    .withDetailType(HardshipReviewDetailType.SOL_COSTS)
                    .withAmount(hardship.getSolicitorCosts().getEstimatedTotal())
                    .withFrequency(Frequency.ANNUALLY)
                    .withAccepted("Y"));
        }
        return apiHardshipDetails;
    }

    public void toDto(ApiPersistHardshipResponse response, HardshipReviewDTO reviewDTO) {
        reviewDTO.getHardshipMetadata().setHardshipReviewId(response.getId());
    }
}
