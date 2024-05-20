package uk.gov.justice.laa.crime.hardship.mapper;

import org.springframework.stereotype.Component;

import uk.gov.justice.laa.crime.common.model.hardship.DeniedIncome;
import uk.gov.justice.laa.crime.common.model.hardship.ExtraExpenditure;
import uk.gov.justice.laa.crime.common.model.hardship.HardshipReview;
import uk.gov.justice.laa.crime.common.model.hardship.SolicitorCosts;
import uk.gov.justice.laa.crime.common.model.hardship.maat_api.ApiHardshipDetail;
import uk.gov.justice.laa.crime.enums.DeniedIncomeDetailCode;
import uk.gov.justice.laa.crime.enums.ExtraExpenditureDetailCode;

import java.util.List;

@Component
public class HardshipDetailMapper implements ResponseMapper<List<ApiHardshipDetail>, HardshipReview> {

    public void toDto(List<ApiHardshipDetail> response, HardshipReview hardship) {
        response
                .forEach(item -> {
                    switch (item.getDetailType()) {
                        case EXPENDITURE -> hardship.getExtraExpenditure().add(
                                new ExtraExpenditure()
                                        .withAmount(item.getAmount())
                                        .withFrequency(item.getFrequency())
                                        .withAccepted(item.getAccepted().equals("Y"))
                                        .withReasonCode(item.getDetailReason())
                                        .withDescription(item.getOtherDescription())
                                        .withItemCode(ExtraExpenditureDetailCode.getFrom(
                                                        item.getDetailCode().getCode()
                                                )
                                        )
                        );
                        case INCOME -> hardship.getDeniedIncome().add(
                                new DeniedIncome()
                                        .withAmount(item.getAmount())
                                        .withFrequency(item.getFrequency())
                                        .withAccepted(item.getAccepted().equals("Y"))
                                        .withDescription(item.getOtherDescription())
                                        .withReasonNote(item.getReasonNote())
                                        .withItemCode(DeniedIncomeDetailCode.getFrom(
                                                        item.getDetailCode().getCode()
                                                )
                                        )
                        );
                        case SOL_COSTS -> hardship.setSolicitorCosts(
                                new SolicitorCosts()
                                        .withEstimatedTotal(item.getAmount())
                        );

                        default -> throw new IllegalStateException("Unexpected value: " + item.getDetailType());
                    }
                });
    }
}
