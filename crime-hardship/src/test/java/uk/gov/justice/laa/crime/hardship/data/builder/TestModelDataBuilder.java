package uk.gov.justice.laa.crime.hardship.data.builder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewDTO;
import uk.gov.justice.laa.crime.hardship.model.*;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class TestModelDataBuilder {

    public static final Integer TEST_REP_ID = 91919;
    public static final String MEANS_ASSESSMENT_TRANSACTION_ID = "7c49ebfe-fe3a-4f2f-8dad-f7b8f03b8327";
    public static final String DETAIL_TYPE = "EXPENDITURE";
    public static final BigDecimal HARDSHIP_SUMMARY = BigDecimal.valueOf(100.12);
    public static final Integer HARDSHIP_ID = 1234;
    public static final String ID = "1234";

    public static final BigDecimal HARDSHIP_AMOUNT = BigDecimal.valueOf(10.0);
    public static final String USER = "TEST-USER";


    public static ApiCalculateHardshipByDetailRequest getApiCalculateHardshipByDetailRequest(boolean isValid) {
        return new ApiCalculateHardshipByDetailRequest()
                .withRepId(isValid ? TEST_REP_ID : null)
                .withLaaTransactionId(MEANS_ASSESSMENT_TRANSACTION_ID)
                .withDetailType(DETAIL_TYPE);
    }

    public static ApiCalculateHardshipByDetailResponse getApiCalculateHardshipByDetailResponse() {
        return new ApiCalculateHardshipByDetailResponse()
                .withHardshipSummary(HARDSHIP_SUMMARY);
    }

    public static List<HardshipReviewDetail> getHardshipReviewDetailList(String accepted, double amount) {
        List<HardshipReviewDetail> list = new ArrayList<>();
        list.add(HardshipReviewDetail.builder()
                .id(HARDSHIP_ID)
                .accepted(accepted)
                .amount(BigDecimal.valueOf(amount))
                .frequency(Frequency.ANNUALLY)
                .detailType(HardshipReviewDetailType.ACTION)
                .build());
        return list;
    }

    public static List<HardshipReviewProgress> buildHardshipReviewProgressList() {
        return List.of(new HardshipReviewProgress(HARDSHIP_ID,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                HardshipReviewProgressAction.FURTHER_INFO,
                HardshipReviewProgressResponse.ADDITIONAL_PROVIDED,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                USER,
                USER)
        );
    }

    public static HardshipReviewProgress buildHardshipReviewProgress() {
        return new HardshipReviewProgress(HARDSHIP_ID,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                HardshipReviewProgressAction.FURTHER_INFO,
                HardshipReviewProgressResponse.ADDITIONAL_PROVIDED,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                USER,
                USER
        );
    }

    public static HardshipReviewProgress buildHardshipReviewProgress(LocalDateTime dateRequested, HardshipReviewProgressResponse progressResponse, LocalDateTime dateRequired ) {
        return new HardshipReviewProgress(HARDSHIP_ID,
                dateRequested,
                dateRequired,
                LocalDateTime.now(),
                LocalDateTime.now(),
                HardshipReviewProgressAction.FURTHER_INFO,
                progressResponse,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                USER,
                USER
        );
    }

    public static HardshipReviewDetailReason buildHardshipReviewDetailReason(){
        return HardshipReviewDetailReason.builder()
                .id(ID)
                .reason("Reason")
                .detailType(HardshipReviewDetailType.ACTION)
                .forceNote("")
                .dateCreated(LocalDateTime.now())
                .userCreated(USER)
                .dateModified(LocalDateTime.now())
                .userModified(USER)
                .accepted(USER).build();
    }

    public static HardshipReviewDetail buildValidHardshipReviewDetail() {
        return HardshipReviewDetail.builder()
                .id(HARDSHIP_ID)
                .description("description")
                .accepted("Y")
                .amount(BigDecimal.valueOf(Double.parseDouble("100.0")))
                .frequency(Frequency.ANNUALLY)
                .detailCode(HardshipReviewDetailCode.ADD_MORTGAGE)
                .detailReason(buildHardshipReviewDetailReason())
                .reasonNote("reasonNote")
                .dateDue(LocalDateTime.now())
                .build();
    }

    public static HardshipReviewDetail buildHardshipReviewDetail(String description, BigDecimal amount, Frequency frequency,
                                                                 HardshipReviewDetailReason detailReason,
                                                                 String reasonNote, LocalDateTime dateDue) {
        return HardshipReviewDetail.builder()
                .id(HARDSHIP_ID)
                .description(description)
                .accepted("Y")
                .amount(amount)
                .frequency(frequency)
                .detailCode(HardshipReviewDetailCode.ADD_MORTGAGE)
                .detailReason(detailReason)
                .reasonNote(reasonNote)
                .dateDue(dateDue)
                .build();
    }

    public static HardshipReviewDTO buildHardshipReviewDTO(String nwrCode, LocalDateTime reviewDate, SolicitorCosts solCosts) {
        return HardshipReviewDTO.builder()
                .id(123)
                .cmuId(456)
                .notes("notes")
                .decisionNotes("decisionNotes")
                .reviewDate(reviewDate)
                .reviewResult("reviewResult")
                .disposableIncome(BigDecimal.valueOf(100.0))
                .disposableIncomeAfterHardship(BigDecimal.valueOf(100.0))
                .newWorkReason(buildNewWorkReason(nwrCode))
                .solicitorCosts(solCosts)
                .reviewStatus(HardshipReviewStatus.COMPLETE)
                .reviewDetails(getHardshipReviewDetailList("Y", 0))
                .reviewProgressItems(buildHardshipReviewProgressList())
                .courtType("courtType").build();
    }


    public static NewWorkReason buildNewWorkReason(String nwrCode) {
        return new NewWorkReason(   nwrCode,
                 "type",
                 "description",
                LocalDateTime.now(),
                 "userCreated",
                LocalDateTime.now(),
                 "userModified",
                1,
                 "enabled",
                 "raGroup",
                 "initialDefault"
        );
    }


}