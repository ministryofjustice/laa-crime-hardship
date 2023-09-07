package uk.gov.justice.laa.crime.hardship.mapper;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.hardship.model.*;
import uk.gov.justice.laa.crime.hardship.model.maat_api.ApiHardshipDetail;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.DeniedIncomeDetailCode;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.ExtraExpenditureDetailCode;

import java.util.List;

@Component
public class HardshipDetailMapper implements ResponseMapper<List<ApiHardshipDetail>, HardshipReview> {

    public void toDto(List<ApiHardshipDetail> response, HardshipReview hardship) {
        response
                .forEach(item -> {
                    switch (item.getType()) {
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
                                        .withItemCode(DeniedIncomeDetailCode.getFrom(
                                                        item.getDetailCode().getCode()
                                                )
                                        )
                        );
                        case FUNDING -> hardship.getOtherFundingSources().add(
                                new OtherFundingSource()
                                        .withAmount(item.getAmount())
                                        .withDueDate(item.getDateDue())
                                        .withDescription(item.getOtherDescription())
                        );
                        case SOL_COSTS -> hardship.setSolicitorCosts(
                                new SolicitorCosts()
                                        .withEstimatedTotal(item.getAmount())
                        );

                        default -> throw new IllegalStateException("Unexpected value: " + item.getType());
                    }
                });
    }
}
