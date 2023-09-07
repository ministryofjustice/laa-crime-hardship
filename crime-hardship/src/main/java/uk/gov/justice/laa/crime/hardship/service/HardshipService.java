package uk.gov.justice.laa.crime.hardship.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.hardship.dto.HardshipResult;
import uk.gov.justice.laa.crime.hardship.dto.maat_api.HardshipReviewDetail;
import uk.gov.justice.laa.crime.hardship.model.ApiCalculateHardshipByDetailRequest;
import uk.gov.justice.laa.crime.hardship.model.ApiCalculateHardshipByDetailResponse;
import uk.gov.justice.laa.crime.hardship.model.HardshipReview;
import uk.gov.justice.laa.crime.hardship.model.SolicitorCosts;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.CourtType;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewResult.FAIL;
import static uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewResult.PASS;

@Slf4j
@Service
@RequiredArgsConstructor
public class HardshipService {

    private final MaatCourtDataService maatCourtDataService;

    public ApiCalculateHardshipByDetailResponse calculateHardshipForDetail(ApiCalculateHardshipByDetailRequest request) {
        ApiCalculateHardshipByDetailResponse apiProcessRepOrderResponse = new ApiCalculateHardshipByDetailResponse();
        BigDecimal hardshipSummary = BigDecimal.ZERO;

        List<HardshipReviewDetail> hardshipReviewDetailList = maatCourtDataService.getHardshipByDetailType(
                        request.getRepId(), request.getDetailType(), request.getLaaTransactionId())
                .stream().filter(hrd -> "Y".equals(hrd.getAccepted()) && BigDecimal.ZERO.compareTo(hrd.getAmount()) != 0).toList();

        for (HardshipReviewDetail hardshipReviewDetail : hardshipReviewDetailList) {
            hardshipSummary = hardshipSummary.add(
                    hardshipReviewDetail.getAmount().multiply(
                            BigDecimal.valueOf(hardshipReviewDetail.getFrequency().getAnnualWeighting()))
            );
        }
        apiProcessRepOrderResponse.setHardshipSummary(hardshipSummary);
        return apiProcessRepOrderResponse;
    }

    public HardshipResult calculateHardship(final HardshipReview hardship, final BigDecimal fullThreshold) {

        BigDecimal total = Stream.of(hardship.getDeniedIncome(), hardship.getExtraExpenditure())
                .flatMap(Collection::stream)
                .map(item -> {
                    if (Boolean.TRUE.equals(item.getAccepted())) {
                        return item.getAmount()
                                .multiply(BigDecimal.valueOf(item.getFrequency().getAnnualWeighting()));
                    }
                    return BigDecimal.ZERO;
                })
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        CourtType courtType = hardship.getCourtType();
        if (courtType == CourtType.MAGISTRATE) {
            SolicitorCosts solicitorCosts = hardship.getSolicitorCosts();
            BigDecimal estimatedTotal = solicitorCosts.getRate()
                    .multiply(BigDecimal.valueOf(solicitorCosts.getHours()))
                    .add(solicitorCosts.getVat())
                    .add(solicitorCosts.getDisbursements());

            total = total.add(estimatedTotal);
        }

        final BigDecimal disposableIncomeAfterHardship =
                hardship.getTotalAnnualDisposableIncome()
                        .subtract(total)
                        .setScale(2, RoundingMode.HALF_UP);

        HardshipReviewResult result = FAIL;
        if (disposableIncomeAfterHardship.compareTo(fullThreshold) <= 0) {
            result = PASS;
        }
        return HardshipResult.builder()
                .result(result)
                .postHardshipDisposableIncome(disposableIncomeAfterHardship)
                .build();
    }
}
