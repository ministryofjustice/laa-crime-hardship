package uk.gov.justice.laa.crime.hardship.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.hardship.dto.HardshipResult;
import uk.gov.justice.laa.crime.hardship.mapper.HardshipDetailMapper;
import uk.gov.justice.laa.crime.hardship.model.ApiCalculateHardshipByDetailResponse;
import uk.gov.justice.laa.crime.hardship.model.HardshipReview;
import uk.gov.justice.laa.crime.hardship.model.SolicitorCosts;
import uk.gov.justice.laa.crime.hardship.model.maat_api.ApiHardshipDetail;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.CourtType;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewDetailType;
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

    private final HardshipDetailMapper detailMapper;
    private final MaatCourtDataService maatCourtDataService;

    public ApiCalculateHardshipByDetailResponse calculateHardshipForDetail(Integer repId,
                                                                           HardshipReviewDetailType detailType,
                                                                           String laaTransactionId) {
        List<ApiHardshipDetail> response =
                maatCourtDataService.getHardshipByDetailType(repId, detailType.getType(), laaTransactionId);

        BigDecimal total = BigDecimal.ZERO;
        if (response != null) {
            HardshipReview hardship = new HardshipReview();
            detailMapper.toDto(response, hardship);
            total = calculateDetails(hardship);
        }
        return new ApiCalculateHardshipByDetailResponse()
                .withHardshipSummary(total);
    }

    public HardshipResult calculateHardship(final HardshipReview hardship, final BigDecimal fullThreshold) {

        BigDecimal total = calculateDetails(hardship);

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

    private static BigDecimal calculateDetails(HardshipReview hardship) {
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
        SolicitorCosts solicitorCosts = hardship.getSolicitorCosts();
        if (solicitorCosts != null
                && (courtType == CourtType.MAGISTRATE || solicitorCosts.getEstimatedTotal() != null)) {
                BigDecimal estimatedTotal = solicitorCosts.getRate()
                        .multiply(BigDecimal.valueOf(solicitorCosts.getHours()))
                        .add(solicitorCosts.getVat())
                        .add(solicitorCosts.getDisbursements());

                solicitorCosts.setEstimatedTotal(estimatedTotal);
                total = total.add(estimatedTotal);
        }
        return total;
    }
}
