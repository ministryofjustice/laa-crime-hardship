package uk.gov.justice.laa.crime.hardship.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.justice.laa.crime.enums.HardshipReviewStatus;
import uk.gov.justice.laa.crime.enums.NewWorkReason;
import uk.gov.justice.laa.crime.hardship.CrimeHardshipApplication;
import uk.gov.justice.laa.crime.hardship.config.CrimeHardshipTestConfiguration;
import uk.gov.justice.laa.crime.hardship.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.hardship.model.*;
import uk.gov.justice.laa.crime.hardship.model.maat_api.ApiHardshipDetail;
import uk.gov.justice.laa.crime.hardship.model.maat_api.ApiPersistHardshipResponse;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.gov.justice.laa.crime.enums.HardshipReviewDetailType.EXPENDITURE;
import static uk.gov.justice.laa.crime.hardship.data.builder.TestModelDataBuilder.*;
import static uk.gov.justice.laa.crime.util.RequestBuilderUtils.buildRequest;
import static uk.gov.justice.laa.crime.util.RequestBuilderUtils.buildRequestGivenContent;

@DirtiesContext
@Import(CrimeHardshipTestConfiguration.class)
@SpringBootTest(classes = CrimeHardshipApplication.class, webEnvironment = DEFINED_PORT)
@AutoConfigureObservability
@AutoConfigureWireMock(port = 9999)
class HardshipIntegrationTest {

    private MockMvc mvc;

    public static final String ENDPOINT_URL_FULL_ASSESSMENT_THRESHOLD = "/api/internal/v1/assessment/means/fullAssessmentThreshold/";
    private static final String ENDPOINT_URL = "/api/internal/v1/hardship";
    public static final String ENDPOINT_URL_GET = ENDPOINT_URL + "/" + HARDSHIP_ID;
    private static final String ENDPOINT_URL_CALCULATE_HARDSHIP = ENDPOINT_URL.concat("/calculate-hardship-for-detail");
    private static final String ENDPOINT_URL_CALC_HARDSHIP = ENDPOINT_URL.concat("/calculate-hardship");

    @Autowired
    private WireMockServer wiremock;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @AfterEach
    void after() {
        wiremock.resetAll();
    }

    @BeforeEach
    public void setup() throws JsonProcessingException {
        stubForOAuth();
        this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .addFilter(springSecurityFilterChain).build();
    }

    @Test
    void givenAEmptyContent_whenUpdateHardshipIsInvoked_thenFailsWithBadRequest() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, "{}", ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAEmptyOAuthToken_whenUpdateHardshipIsInvoked_thenFailsWithUnauthorizedAccess() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, "{}", ENDPOINT_URL, false))
                .andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    void givenAEmptyContent_whenCalculateHardshipForDetailIsInvoked_thenFailsWithBadRequest() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, "{}", ENDPOINT_URL_CALCULATE_HARDSHIP))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAEmptyOAuthToken_whenCalculateHardshipForDetailIsInvoked_thenFailsWithUnauthorizedAccess() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, "{}", ENDPOINT_URL_CALCULATE_HARDSHIP, false))
                .andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    void givenValidHardshipId_whenFindIsInvoked_thenHardshipReviewIsReturned() throws Exception {
        ApiFindHardshipResponse response = TestModelDataBuilder.getApiFindHardshipResponse();
        wiremock.stubFor(get(urlEqualTo("/api/internal/v1/assessment/hardship/" + HARDSHIP_ID)).willReturn(
                WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(objectMapper.writeValueAsString(response))));

        mvc.perform(buildRequest(HttpMethod.GET, ENDPOINT_URL + "/" + HARDSHIP_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(response.getId()));
    }

    @Test
    void givenEmptyAuthToken_whenFindIsInvoked_thenFailsWithUnauthorisedRequest() throws Exception {
        mvc.perform(buildRequest(HttpMethod.GET, ENDPOINT_URL + "/" + HARDSHIP_ID, false))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenUnknownHardshipReviewId_whenFindIsInvoked_thenInternalSeverErrorResponse() throws Exception {
        wiremock.stubFor(get(urlEqualTo("/api/internal/v1/assessment/hardship/" + HARDSHIP_ID)).willReturn(
                WireMock.badRequest()));

        mvc.perform(buildRequest(HttpMethod.GET, ENDPOINT_URL_GET))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Call to service MAAT-API failed."));
    }

    @Test
    void givenAEmptyContent_whenCreateHardshipIsInvoked_thenFailsWithBadRequest() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, "{}", ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAEmptyOAuthToken_whenCreateHardshipIsInvoked_thenFailsWithUnauthorizedAccess() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, "{}", ENDPOINT_URL, false))
                .andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    void givenValidRequest_whenUpdateHardshipIsInvoked_thenOkResponse() throws Exception {
        ApiPerformHardshipRequest request = TestModelDataBuilder.getApiPerformHardshipRequest();

        String requestBody = objectMapper.writeValueAsString(request);

        ApiPersistHardshipResponse response = TestModelDataBuilder.getApiPersistHardshipResponse();

        wiremock.stubFor(get(urlEqualTo(ENDPOINT_URL_FULL_ASSESSMENT_THRESHOLD
                + request.getHardship().getReviewDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))).willReturn(
                WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(objectMapper.writeValueAsString(BigDecimal.TEN))));

        wiremock.stubFor(put(urlEqualTo("/api/internal/v1/assessment/hardship")).willReturn(
                WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(objectMapper.writeValueAsString(response))));

        wiremock.stubFor(get(urlEqualTo("/api/internal/v1/assessment/financial-assessments/" +
                request.getHardshipMetadata().getFinancialAssessmentId())).willReturn(
                WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(objectMapper.writeValueAsString(TestModelDataBuilder.getFinancialAssessmentDTO()))));

        wiremock.stubFor(get(urlEqualTo("/api/internal/v1/assessment/hardship/" +
                request.getHardshipMetadata().getHardshipReviewId())).willReturn(
                WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(objectMapper.writeValueAsString(new ApiFindHardshipResponse().withStatus(
                                HardshipReviewStatus.IN_PROGRESS)))
        ));

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestBody, ENDPOINT_URL)).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.hardshipReviewId").value(1000));

        verify(exactly(1), putRequestedFor(urlEqualTo("/api/internal/v1/assessment/hardship")));
    }

    @Test
    void givenInvalidRequest_whenUpdateHardshipIsInvoked_thenFailsWithBadRequest() throws Exception {
        ApiPerformHardshipRequest request = new ApiPerformHardshipRequest(new HardshipReview().
                withSolicitorCosts(new SolicitorCosts().withRate(BigDecimal.ONE).withHours(null)),
                new HardshipMetadata().withReviewReason(NewWorkReason.NEW));

        String requestBody = objectMapper.writeValueAsString(request);

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestBody, ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenValidRequest_whenCreateHardshipIsInvoked_thenOkResponse() throws Exception {
        ApiPerformHardshipRequest request = TestModelDataBuilder.getApiPerformHardshipRequest();

        String requestBody = objectMapper.writeValueAsString(request);

        ApiPersistHardshipResponse response = TestModelDataBuilder.getApiPersistHardshipResponse();

        wiremock.stubFor(post(urlEqualTo("/api/internal/v1/assessment/hardship")).willReturn(
                WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(objectMapper.writeValueAsString(response))));

        wiremock.stubFor(get(urlEqualTo(ENDPOINT_URL_FULL_ASSESSMENT_THRESHOLD
                + request.getHardship().getReviewDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))).willReturn(
                WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(objectMapper.writeValueAsString(BigDecimal.TEN))));

        wiremock.stubFor(get(urlEqualTo("/api/internal/v1/assessment/financial-assessments/" +
                request.getHardshipMetadata().getFinancialAssessmentId())).willReturn(
                WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(objectMapper.writeValueAsString(TestModelDataBuilder.getFinancialAssessmentDTO()))));

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestBody, ENDPOINT_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hardshipReviewId").value(1000));
        verify(exactly(1), postRequestedFor(urlEqualTo("/api/internal/v1/assessment/hardship")));
    }

    @Test
    void givenInvalidRequest_whenCreateHardshipIsInvoked_thenFailsWithBadRequest() throws Exception {
        ApiPerformHardshipRequest request = new ApiPerformHardshipRequest(new HardshipReview().
                withSolicitorCosts(new SolicitorCosts().withRate(BigDecimal.ONE).withHours(null)),
                new HardshipMetadata().withReviewReason(NewWorkReason.NEW));

        String requestBody = objectMapper.writeValueAsString(request);

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestBody, ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }


    @Test
    void givenValidRequest_whenCalculateHardshipForDetailIsInvoked_thenOkResponse() throws Exception {
        ApiCalculateHardshipByDetailRequest request =
                TestModelDataBuilder.getApiCalculateHardshipByDetailRequest(true, EXPENDITURE);

        String requestBody = objectMapper.writeValueAsString(request);

        List<ApiHardshipDetail> hardshipDetails = TestModelDataBuilder.getApiHardshipReviewDetails(EXPENDITURE);

        wiremock.stubFor(get(urlEqualTo("/api/internal/v1/assessment/hardship/repId/" + TEST_REP_ID + "/detailType/" + DETAIL_TYPE)).willReturn(
                WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(objectMapper.writeValueAsString(hardshipDetails))));

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestBody, ENDPOINT_URL_CALCULATE_HARDSHIP))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.hardshipSummary").value(260));
        verify(exactly(1), getRequestedFor(urlEqualTo("/api/internal/v1/assessment/hardship/repId/" + TEST_REP_ID + "/detailType/" + DETAIL_TYPE)));
    }

    @Test
    void givenInvalidRequest_whenCalculateHardshipForDetailIsInvoked_thenFailsWithBadRequest() throws Exception {
        ApiCalculateHardshipByDetailRequest request = new ApiCalculateHardshipByDetailRequest();

        String requestBody = objectMapper.writeValueAsString(request);

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestBody, ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAnEmptyContent_whenRollbackHardshipIsInvoked_thenFailsWithBadRequest() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.PATCH, "{}", ENDPOINT_URL + "/null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAnEmptyOAuthToken_whenUpdateHardshipIsInvoked_thenFailsWithUnauthorizedAccess() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, "{}", ENDPOINT_URL + "/rollback", false))
                .andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    void givenAValidRequest_whenRollbackHardshipIsInvoked_thenOkResponse() throws Exception {
        wiremock.stubFor(
                patch(urlEqualTo("/api/internal/v1/assessment/hardship/" + HARDSHIP_ID)).willReturn(
                        WireMock.ok()
                                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                )
        );

        mvc.perform(buildRequestGivenContent(HttpMethod.PATCH, "", ENDPOINT_URL_GET)).andExpect(status().isOk());
        verify(exactly(1), patchRequestedFor(urlEqualTo("/api/internal/v1/assessment/hardship/" + HARDSHIP_ID)));
    }

    private void stubForOAuth() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> token = Map.of(
                "expires_in", 3600,
                "token_type", "Bearer",
                "access_token", UUID.randomUUID()
        );

        wiremock.stubFor(
                post("/oauth2/token").willReturn(
                        WireMock.ok()
                                .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                                .withBody(mapper.writeValueAsString(token))
                )
        );
    }


    @Test
    void givenAEmptyContent_whenCalculateHardshipIsInvoked_thenFailsWithBadRequest() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, "{}", ENDPOINT_URL_CALC_HARDSHIP))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAEmptyOAuthToken_whenCalculateHardshipIsInvoked_thenFailsWithUnauthorizedAccess() throws Exception {
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, "{}", ENDPOINT_URL_CALC_HARDSHIP, false))
                .andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    void givenValidRequest_whenCalculateHardshipIsInvoked_thenOkResponse() throws Exception {
        ApiCalculateHardshipRequest request = TestModelDataBuilder.getApiCalculateHardshipRequest();
        request.getHardship().setReviewDate(TestModelDataBuilder.ASSESSMENT_DATE);
        String requestBody = objectMapper.writeValueAsString(request);

        wiremock.stubFor(get(urlEqualTo(ENDPOINT_URL_FULL_ASSESSMENT_THRESHOLD
                + TestModelDataBuilder.ASSESSMENT_DATE.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))).willReturn(
                WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(objectMapper.writeValueAsString(BigDecimal.TEN))));

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestBody, ENDPOINT_URL_CALC_HARDSHIP))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.postHardshipDisposableIncome").value(-2380.0));
        verify(exactly(1), getRequestedFor(urlEqualTo("/api/internal/v1/assessment/means/fullAssessmentThreshold/2022-12-14")));
    }

}