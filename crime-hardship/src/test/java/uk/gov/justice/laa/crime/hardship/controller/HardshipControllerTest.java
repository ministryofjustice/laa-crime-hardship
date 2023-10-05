package uk.gov.justice.laa.crime.hardship.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.crime.commons.exception.APIClientException;
import uk.gov.justice.laa.crime.hardship.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewDTO;
import uk.gov.justice.laa.crime.hardship.mapper.HardshipMapper;
import uk.gov.justice.laa.crime.hardship.model.ApiPerformHardshipRequest;
import uk.gov.justice.laa.crime.hardship.model.ApiPerformHardshipResponse;
import uk.gov.justice.laa.crime.hardship.service.HardshipCalculationService;
import uk.gov.justice.laa.crime.hardship.service.HardshipService;
import uk.gov.justice.laa.crime.hardship.service.HardshipValidationService;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewResult;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.gov.justice.laa.crime.hardship.util.RequestBuilderUtils.buildRequestGivenContent;

@WebMvcTest(HardshipController.class)
@AutoConfigureMockMvc(addFilters = false)
class HardshipControllerTest {

    private static final String ENDPOINT_URL = "/api/internal/v1/hardship";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HardshipMapper hardshipMapper;

    @MockBean
    private HardshipService hardshipService;

    @MockBean
    private HardshipValidationService validationService;

    @MockBean
    private HardshipCalculationService calculationService;

    @Test
    void givenValidRequest_whenCreateIsInvoked_thenOkResponseIsReturned() throws Exception {
        ApiPerformHardshipRequest request = new ApiPerformHardshipRequest()
                .withHardship(TestModelDataBuilder.getHardshipReview())
                .withHardshipMetadata(TestModelDataBuilder.getHardshipMetadata());

        String requestBody = objectMapper.writeValueAsString(request);

        ApiPerformHardshipResponse response = new ApiPerformHardshipResponse()
                .withHardshipReviewId(1000)
                .withReviewResult(HardshipReviewResult.PASS)
                .withDisposableIncome(BigDecimal.valueOf(3500))
                .withPostHardshipDisposableIncome(BigDecimal.TEN);

        when(hardshipMapper.fromDto(any(HardshipReviewDTO.class)))
                .thenReturn(response);

        when(hardshipService.create(any(HardshipReviewDTO.class), anyString()))
                .thenReturn(new HardshipReviewDTO());

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestBody, ENDPOINT_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.hardshipReviewId").value(1000));
    }

    @Test
    void givenInvalidRequest_whenCreateIsInvoked_thenBadRequestResponseIsReturned() throws Exception {
        ApiPerformHardshipRequest request = new ApiPerformHardshipRequest()
                .withHardship(TestModelDataBuilder.getHardshipReview());

        String requestBody = objectMapper.writeValueAsString(request);

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestBody, ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenFailedApiCall_whenCreateIsInvoked_thenInternalServerErrorResponseIsReturned() throws Exception {
        ApiPerformHardshipRequest request = new ApiPerformHardshipRequest()
                .withHardship(TestModelDataBuilder.getHardshipReview())
                .withHardshipMetadata(TestModelDataBuilder.getHardshipMetadata());

        when(hardshipService.create(any(HardshipReviewDTO.class), anyString()))
                .thenThrow(new APIClientException("Call to Court Data API failed."));

        String requestBody = objectMapper.writeValueAsString(request);

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestBody, ENDPOINT_URL))
                .andExpect(status().isInternalServerError());
    }
}
