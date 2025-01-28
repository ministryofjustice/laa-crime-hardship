package uk.gov.justice.laa.crime.hardship.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import uk.gov.justice.laa.crime.common.model.hardship.ApiCalculateHardshipByDetailRequest;
import uk.gov.justice.laa.crime.common.model.hardship.ApiCalculateHardshipByDetailResponse;
import uk.gov.justice.laa.crime.common.model.hardship.ApiCalculateHardshipRequest;
import uk.gov.justice.laa.crime.common.model.hardship.ApiFindHardshipResponse;
import uk.gov.justice.laa.crime.common.model.hardship.ApiPerformHardshipRequest;
import uk.gov.justice.laa.crime.common.model.hardship.ApiPerformHardshipResponse;
import uk.gov.justice.laa.crime.common.model.hardship.HardshipReview;
import uk.gov.justice.laa.crime.enums.HardshipReviewResult;
import uk.gov.justice.laa.crime.hardship.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewDTO;
import uk.gov.justice.laa.crime.hardship.mapper.HardshipMapper;
import uk.gov.justice.laa.crime.hardship.service.CrimeMeansAssessmentService;
import uk.gov.justice.laa.crime.hardship.service.HardshipCalculationService;
import uk.gov.justice.laa.crime.hardship.service.HardshipService;
import uk.gov.justice.laa.crime.hardship.tracing.TraceIdHandler;
import uk.gov.justice.laa.crime.hardship.validation.HardshipValidationService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.crime.enums.HardshipReviewDetailType.EXPENDITURE;

@WebMvcTest(HardshipController.class)
@AutoConfigureMockMvc(addFilters = false)
class HardshipControllerTest {

    private static final String ENDPOINT_URL = "/api/internal/v1/hardship";
    private static final String ENDPOINT_URL_CALCULATE_HARDSHIP = ENDPOINT_URL.concat("/calculate-hardship-for-detail");
    private static final String ENDPOINT_URL_CALC_HARDSHIP = ENDPOINT_URL.concat("/calculate-hardship");
    private static final String ENDPOINT_URL_GET_HARDSHIP = ENDPOINT_URL + "/" + TestModelDataBuilder.HARDSHIP_ID;
    @MockBean
    TraceIdHandler traceIdHandler;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private HardshipMapper hardshipMapper;
    @MockBean
    private HardshipService hardshipService;
    @MockBean
    private HardshipCalculationService hardshipCalculationService;
    @MockBean
    private HardshipValidationService validationService;
    @MockBean
    private CrimeMeansAssessmentService crimeMeansAssessmentService;

//    @Test
//    void givenValidHardshipReviewId_whenFindIsInvoked_thenOkResponseIsReturned() throws Exception {
//        ApiFindHardshipResponse response = TestModelDataBuilder.getApiFindHardshipResponse();
//        when(hardshipService.find(anyInt())).thenReturn(response);
//
//        mvc.perform(MockMvcRequestBuilders.get(ENDPOINT_URL_GET_HARDSHIP))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").value(response.getId()));
//    }

    @Test
    void givenInvalidHardshipReviewId_whenFindIsInvoked_thenBadRequestResponseIsReturned() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(ENDPOINT_URL + "/invalidId"))
                .andExpect(status().isBadRequest());
    }

//    @Test
//    void givenFailedApiCall_whenFindIsInvoked_thenInternalServerErrorIsReturned() throws Exception {
//        when(hardshipService.find(anyInt()))
//                .thenThrow(WebClientRequestException.class);
//
//        mvc.perform(MockMvcRequestBuilders.get(ENDPOINT_URL_GET_HARDSHIP))
//                .andExpect(status().isInternalServerError());
//    }

    @Test
    void givenValidRequest_whenCreateIsInvoked_thenOkResponseIsReturned() throws Exception {
        ApiPerformHardshipRequest request = TestModelDataBuilder.getApiPerformHardshipRequest();

        String requestBody = objectMapper.writeValueAsString(request);

        ApiPerformHardshipResponse response = TestModelDataBuilder.getApiPerformHardshipResponse();

        when(hardshipMapper.fromDto(any(HardshipReviewDTO.class)))
                .thenReturn(response);

        when(hardshipService.create(any(HardshipReviewDTO.class)))
                .thenReturn(new HardshipReviewDTO());

        mvc.perform(MockMvcRequestBuilders.post(ENDPOINT_URL).content(requestBody).contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.hardshipReviewId").value(1000));
    }

    @Test
    void givenInvalidRequest_whenCreateIsInvoked_thenBadRequestResponseIsReturned() throws Exception {
        ApiPerformHardshipRequest request = new ApiPerformHardshipRequest()
                .withHardship(TestModelDataBuilder.getHardshipReview());

        String requestBody = objectMapper.writeValueAsString(request);

        mvc.perform(MockMvcRequestBuilders.post(ENDPOINT_URL).content(requestBody).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenFailedApiCall_whenCreateIsInvoked_thenInternalServerErrorResponseIsReturned() throws Exception {
        ApiPerformHardshipRequest request = TestModelDataBuilder.getApiPerformHardshipRequest();

        when(hardshipService.create(any(HardshipReviewDTO.class)))
                .thenThrow(WebClientRequestException.class);

        String requestBody = objectMapper.writeValueAsString(request);

        mvc.perform(MockMvcRequestBuilders.post(ENDPOINT_URL).content(requestBody).contentType(APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void givenValidRequest_whenUpdateIsInvoked_thenOkResponseIsReturned() throws Exception {
        ApiPerformHardshipRequest request = TestModelDataBuilder.getApiPerformHardshipRequest();

        String requestBody = objectMapper.writeValueAsString(request);

        ApiPerformHardshipResponse response = TestModelDataBuilder.getApiPerformHardshipResponse();

        when(hardshipMapper.fromDto(any(HardshipReviewDTO.class)))
                .thenReturn(response);

        when(hardshipService.update(any(HardshipReviewDTO.class)))
                .thenReturn(new HardshipReviewDTO());

        mvc.perform(MockMvcRequestBuilders.put(ENDPOINT_URL).content(requestBody).contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.hardshipReviewId").value(1000));
    }

    @Test
    void givenInvalidRequest_whenUpdateIsInvoked_thenBadRequestResponseIsReturned() throws Exception {
        ApiPerformHardshipRequest request = new ApiPerformHardshipRequest()
                .withHardship(TestModelDataBuilder.getHardshipReview());

        String requestBody = objectMapper.writeValueAsString(request);

        mvc.perform(MockMvcRequestBuilders.put(ENDPOINT_URL).content(requestBody).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenFailedApiCall_whenUpdateIsInvoked_thenInternalServerErrorResponseIsReturned() throws Exception {
        ApiPerformHardshipRequest request = TestModelDataBuilder.getApiPerformHardshipRequest();

        when(hardshipService.update(any(HardshipReviewDTO.class)))
                .thenThrow(WebClientRequestException.class);

        String requestBody = objectMapper.writeValueAsString(request);

        mvc.perform(MockMvcRequestBuilders.put(ENDPOINT_URL).content(requestBody).contentType(APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void givenValidRequest_whenCalculateHardshipForDetailIsInvoked_thenOkResponseIsReturned() throws Exception {
        ApiCalculateHardshipByDetailRequest request =
                TestModelDataBuilder.getApiCalculateHardshipByDetailRequest(true, EXPENDITURE);

        String requestBody = objectMapper.writeValueAsString(request);

        ApiCalculateHardshipByDetailResponse response = TestModelDataBuilder.getApiCalculateHardshipByDetailResponse();

        when(hardshipCalculationService.calculateHardshipForDetail(anyInt(), any()))
                .thenReturn(response);

        mvc.perform(MockMvcRequestBuilders.post(ENDPOINT_URL_CALCULATE_HARDSHIP).content(requestBody).contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.hardshipSummary").value(3500));
    }

    @Test
    void givenInvalidRequest_whenCalculateHardshipForDetailIsInvoke_thenBadRequestResponseIsReturned() throws Exception {
        ApiCalculateHardshipByDetailRequest request = new ApiCalculateHardshipByDetailRequest();

        String requestBody = objectMapper.writeValueAsString(request);

        mvc.perform(MockMvcRequestBuilders.post(ENDPOINT_URL_CALCULATE_HARDSHIP).content(requestBody).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenFailedApiCall_whenCalculateHardshipForDetailIsInvoke_thenInternalServerErrorResponseIsReturned() throws Exception {
        ApiCalculateHardshipByDetailRequest request =
                TestModelDataBuilder.getApiCalculateHardshipByDetailRequest(true, EXPENDITURE);

        String requestBody = objectMapper.writeValueAsString(request);

        when(hardshipCalculationService.calculateHardshipForDetail(any(), any()))
                .thenThrow(WebClientRequestException.class);

        mvc.perform(MockMvcRequestBuilders.post(ENDPOINT_URL_CALCULATE_HARDSHIP).content(requestBody).contentType(APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void givenValidRequest_whenRollbackIsInvoked_thenOkResponseIsReturned() throws Exception {
        doNothing().when(hardshipService).rollback(anyInt());
        mvc.perform(MockMvcRequestBuilders.patch(ENDPOINT_URL_GET_HARDSHIP))
                .andExpect(status().isOk());
    }

    @Test
    void givenInvalidRequest_whenRollbackIsInvoked_thenBadRequestResponseIsReturned() throws Exception {
        mvc.perform(MockMvcRequestBuilders.patch(ENDPOINT_URL + "/null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenFailedApiCall_whenRollbackIsInvoked_thenInternalServerErrorResponseIsReturned() throws Exception {
        doThrow(WebClientRequestException.class)
                .when(hardshipService).rollback(anyInt());

        mvc.perform(MockMvcRequestBuilders.patch(ENDPOINT_URL_GET_HARDSHIP))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void givenValidRequest_whenCalculateHardshipIsInvoked_thenOkResponseIsReturned() throws Exception {
        ApiCalculateHardshipRequest request = TestModelDataBuilder.getApiCalculateHardshipRequest();
        String requestBody = objectMapper.writeValueAsString(request);

        when(crimeMeansAssessmentService.getFullAssessmentThreshold(any(LocalDateTime.class)))
                .thenReturn(TestModelDataBuilder.FULL_THRESHOLD);

        when(hardshipCalculationService.calculateHardship(any(HardshipReview.class), any()))
                .thenReturn(TestModelDataBuilder.getHardshipResult(HardshipReviewResult.PASS));

        mvc.perform(MockMvcRequestBuilders.post(ENDPOINT_URL_CALC_HARDSHIP).content(requestBody).contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.postHardshipDisposableIncome").value(TestModelDataBuilder.POST_HARDSHIP_DISPOSABLE_INCOME));
    }

    @Test
    void givenInvalidRequest_whenCalculateHardshipIsInvoked_thenBadRequestResponseIsReturned() throws Exception {
        ApiCalculateHardshipRequest request = TestModelDataBuilder.getApiCalculateHardshipRequest();
        request.getHardship().setReviewDate(null);

        String requestBody = objectMapper.writeValueAsString(request);

        mvc.perform(MockMvcRequestBuilders.post(ENDPOINT_URL_CALC_HARDSHIP).content(requestBody).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenFailedApiCall_whenCalculateHardshipIsInvoked_thenInternalServerErrorResponseIsReturned() throws Exception {
        ApiCalculateHardshipRequest request = new ApiCalculateHardshipRequest()
                .withHardship(TestModelDataBuilder.getHardshipReview());

        String requestBody = objectMapper.writeValueAsString(request);

        when(crimeMeansAssessmentService.getFullAssessmentThreshold(any(LocalDateTime.class)))
                .thenThrow(WebClientRequestException.class);

        mvc.perform(MockMvcRequestBuilders.post(ENDPOINT_URL_CALC_HARDSHIP).content(requestBody).contentType(APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

}
