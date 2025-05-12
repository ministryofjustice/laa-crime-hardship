package uk.gov.justice.laa.crime.hardship.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.Json;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;
import uk.gov.justice.laa.crime.common.model.hardship.*;
import uk.gov.justice.laa.crime.common.model.hardship.maat_api.ApiPersistHardshipResponse;
import uk.gov.justice.laa.crime.dto.ErrorDTO;
import uk.gov.justice.laa.crime.enums.HardshipReviewStatus;
import uk.gov.justice.laa.crime.enums.NewWorkReason;
import uk.gov.justice.laa.crime.hardship.CrimeHardshipApplication;
import uk.gov.justice.laa.crime.hardship.config.CrimeHardshipTestConfiguration;
import uk.gov.justice.laa.crime.hardship.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.hardship.tracing.TraceIdHandler;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.crime.enums.HardshipReviewDetailType.EXPENDITURE;
import static uk.gov.justice.laa.crime.hardship.data.builder.TestModelDataBuilder.*;

@EnableWireMock
@DirtiesContext
@AutoConfigureObservability
@Import(CrimeHardshipTestConfiguration.class)
@SpringBootTest(classes = CrimeHardshipApplication.class, webEnvironment = DEFINED_PORT)
class HardshipIntegrationTest {

    private MockMvc mvc;

    private static final String BEARER_TOKEN = "Bearer token";
    private static final String ENDPOINT_URL = "/api/internal/v1/hardship";
    private static final String ENDPOINT_URL_GET = ENDPOINT_URL + "/" + HARDSHIP_ID;
    private static final String ENDPOINT_URL_FULL_ASSESSMENT_THRESHOLD = "/fullAssessmentThreshold/";
    private static final String ENDPOINT_URL_CALCULATE_HARDSHIP = ENDPOINT_URL.concat("/calculate-hardship-for-detail");
    private static final String ENDPOINT_URL_CALC_HARDSHIP = ENDPOINT_URL.concat("/calculate-hardship");

    @InjectWireMock
    private static WireMockServer wiremock;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private TraceIdHandler traceIdHandler;


    @BeforeEach
    void setup() throws JsonProcessingException {
        stubForOAuth();
        this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .addFilter(springSecurityFilterChain).build();
    }

    @Test
    void givenAEmptyContent_whenUpdateHardshipIsInvoked_thenFailsWithBadRequest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.put(ENDPOINT_URL).content("{}").contentType(APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAEmptyOAuthToken_whenUpdateHardshipIsInvoked_thenFailsWithUnauthorizedAccess() throws Exception {
        mvc.perform(MockMvcRequestBuilders.put(ENDPOINT_URL).content("{}").contentType(APPLICATION_JSON))
                .andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    void givenAEmptyContent_whenCalculateHardshipForDetailIsInvoked_thenFailsWithBadRequest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(ENDPOINT_URL_CALCULATE_HARDSHIP).content("{}").contentType(APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAEmptyOAuthToken_whenCalculateHardshipForDetailIsInvoked_thenFailsWithUnauthorizedAccess() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(ENDPOINT_URL_CALCULATE_HARDSHIP).content("{}").contentType(APPLICATION_JSON))
                .andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    void givenValidHardshipId_whenFindIsInvoked_thenHardshipReviewIsReturned() throws Exception {
        ApiFindHardshipResponse response = TestModelDataBuilder.getApiFindHardshipResponse();
        wiremock.stubFor(get(urlEqualTo("/hardship/" + HARDSHIP_ID))
                                 .willReturn(
                                         WireMock.ok()
                                                 .withHeader("Content-Type", String.valueOf(APPLICATION_JSON))
                                                 .withBody(objectMapper.writeValueAsString(response))));

        mvc.perform(MockMvcRequestBuilders.get(ENDPOINT_URL + "/" + HARDSHIP_ID)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(response.getId()));
    }

    @Test
    void givenEmptyAuthToken_whenFindIsInvoked_thenFailsWithUnauthorisedRequest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(ENDPOINT_URL + "/" + HARDSHIP_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenUnknownHardshipReviewId_whenFindIsInvoked_thenFailsWithBadRequest() throws Exception {
        wiremock.stubFor(get(urlEqualTo("/hardship/" + HARDSHIP_ID)).willReturn(
                WireMock.badRequest()));

        mvc.perform(MockMvcRequestBuilders.get(ENDPOINT_URL_GET)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("400 Bad Request")))
                .andExpect(jsonPath("$.message", containsString("/hardship/" + HARDSHIP_ID)));
    }

    @Test
    void givenInvalidHardshipReview_whenFindIsInvoked_thenErrorResponseIsReturned() throws Exception {
        String errorMessage = HARDSHIP_ID + " is invalid";
        ErrorDTO errorDTO = ErrorDTO.builder()
                .code(HttpStatus.BAD_REQUEST.name())
                .message(errorMessage)
                .build();
        wiremock.stubFor(get(urlEqualTo("/hardship/" + HARDSHIP_ID)).willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                        .withStatus(400).withBody(Json.write(errorDTO)).withHeader("Content-Type", "application/json")
        ));
        mvc.perform(MockMvcRequestBuilders.get(ENDPOINT_URL_GET)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    void givenAEmptyContent_whenCreateHardshipIsInvoked_thenFailsWithBadRequest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(ENDPOINT_URL).contentType(APPLICATION_JSON).content("{}")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAEmptyOAuthToken_whenCreateHardshipIsInvoked_thenFailsWithUnauthorizedAccess() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(ENDPOINT_URL).contentType(APPLICATION_JSON).content("{}"))
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
                        .withHeader("Content-Type", String.valueOf(APPLICATION_JSON))
                        .withBody(objectMapper.writeValueAsString(BigDecimal.TEN))));

        wiremock.stubFor(put(urlEqualTo("/hardship")).willReturn(
                WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(APPLICATION_JSON))
                        .withBody(objectMapper.writeValueAsString(response))));

        wiremock.stubFor(get(urlEqualTo("/financial-assessments/" +
                request.getHardshipMetadata().getFinancialAssessmentId())).willReturn(
                WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(APPLICATION_JSON))
                        .withBody(objectMapper.writeValueAsString(TestModelDataBuilder.getFinancialAssessmentDTO()))));

        wiremock.stubFor(get(urlEqualTo("/hardship/" +
                request.getHardshipMetadata().getHardshipReviewId())).willReturn(
                WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(APPLICATION_JSON))
                        .withBody(objectMapper.writeValueAsString(new ApiFindHardshipResponse().withStatus(
                                HardshipReviewStatus.IN_PROGRESS)))
        ));

        mvc.perform(MockMvcRequestBuilders.put(ENDPOINT_URL).content(requestBody).contentType(APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.hardshipReviewId").value(1000));

        verify(exactly(1), putRequestedFor(urlEqualTo("/hardship")));
    }

    @Test
    void givenInvalidRequest_whenUpdateHardshipIsInvoked_thenFailsWithBadRequest() throws Exception {
        ApiPerformHardshipRequest request = new ApiPerformHardshipRequest(new HardshipReview().
                withSolicitorCosts(new SolicitorCosts().withRate(BigDecimal.ONE).withHours(null)),
                new HardshipMetadata().withReviewReason(NewWorkReason.NEW));

        String requestBody = objectMapper.writeValueAsString(request);

        mvc.perform(MockMvcRequestBuilders.put(ENDPOINT_URL).contentType(APPLICATION_JSON).content(requestBody)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenValidRequest_whenCreateHardshipIsInvoked_thenOkResponse() throws Exception {
        ApiPerformHardshipRequest request = TestModelDataBuilder.getApiPerformHardshipRequest();

        String requestBody = objectMapper.writeValueAsString(request);

        ApiPersistHardshipResponse response = TestModelDataBuilder.getApiPersistHardshipResponse();

        wiremock.stubFor(post(urlEqualTo("/hardship")).willReturn(
                WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(APPLICATION_JSON))
                        .withBody(objectMapper.writeValueAsString(response))));

        wiremock.stubFor(get(urlEqualTo(ENDPOINT_URL_FULL_ASSESSMENT_THRESHOLD
                + request.getHardship().getReviewDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))).willReturn(
                WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(APPLICATION_JSON))
                        .withBody(objectMapper.writeValueAsString(BigDecimal.TEN))));

        wiremock.stubFor(get(urlEqualTo("/financial-assessments/" +
                request.getHardshipMetadata().getFinancialAssessmentId())).willReturn(
                WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(APPLICATION_JSON))
                        .withBody(objectMapper.writeValueAsString(TestModelDataBuilder.getFinancialAssessmentDTO()))));

        mvc.perform(MockMvcRequestBuilders.post(ENDPOINT_URL).content(requestBody).contentType(APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hardshipReviewId").value(1000));
        verify(exactly(1), postRequestedFor(urlEqualTo("/hardship")));
    }

    @Test
    void givenInvalidRequest_whenCreateHardshipIsInvoked_thenFailsWithBadRequest() throws Exception {
        ApiPerformHardshipRequest request = new ApiPerformHardshipRequest(new HardshipReview().
                withSolicitorCosts(new SolicitorCosts().withRate(BigDecimal.ONE).withHours(null)),
                new HardshipMetadata().withReviewReason(NewWorkReason.NEW));

        String requestBody = objectMapper.writeValueAsString(request);

        mvc.perform(MockMvcRequestBuilders.post(ENDPOINT_URL).contentType(APPLICATION_JSON).content(requestBody)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN))
                .andExpect(status().isBadRequest());
    }


    @Test
    void givenValidRequest_whenCalculateHardshipForDetailIsInvoked_thenOkResponse() throws Exception {
        ApiCalculateHardshipByDetailRequest request =
                TestModelDataBuilder.getApiCalculateHardshipByDetailRequest(true, EXPENDITURE);

        String requestBody = objectMapper.writeValueAsString(request);

        List<ApiHardshipDetail> hardshipDetails = TestModelDataBuilder.getApiHardshipReviewDetails(EXPENDITURE);

        wiremock.stubFor(get(urlEqualTo("/hardship/repId/" + TEST_REP_ID + "/detailType/" + DETAIL_TYPE)).willReturn(
                WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(APPLICATION_JSON))
                        .withBody(objectMapper.writeValueAsString(hardshipDetails))));

        mvc.perform(MockMvcRequestBuilders.post(ENDPOINT_URL_CALCULATE_HARDSHIP).content(requestBody).contentType(APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON)).andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.hardshipSummary").value(260));
        verify(exactly(1), getRequestedFor(urlEqualTo("/hardship/repId/" + TEST_REP_ID + "/detailType/" + DETAIL_TYPE)));
    }

    @Test
    void givenInvalidRequest_whenCalculateHardshipForDetailIsInvoked_thenFailsWithBadRequest() throws Exception {
        ApiCalculateHardshipByDetailRequest request = new ApiCalculateHardshipByDetailRequest();

        String requestBody = objectMapper.writeValueAsString(request);

        mvc.perform(MockMvcRequestBuilders.put(ENDPOINT_URL).contentType(APPLICATION_JSON).content(requestBody)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAnEmptyContent_whenRollbackHardshipIsInvoked_thenFailsWithBadRequest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.patch(ENDPOINT_URL + "/null").content("{}").contentType(APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAnEmptyOAuthToken_whenRollbackIsInvoked_thenFailsWithUnauthorizedAccess() throws Exception {
        mvc.perform(MockMvcRequestBuilders.put(ENDPOINT_URL + "/rollback").content("{}").contentType(APPLICATION_JSON))
                .andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    void givenAValidRequest_whenRollbackHardshipIsInvoked_thenOkResponse() throws Exception {
        wiremock.stubFor(
                patch(urlEqualTo("/hardship/" + HARDSHIP_ID)).willReturn(
                        WireMock.ok()
                                .withHeader("Content-Type", String.valueOf(APPLICATION_JSON))
                )
        );

        mvc.perform(MockMvcRequestBuilders.patch(ENDPOINT_URL_GET).contentType(APPLICATION_JSON).content("")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN))
                .andExpect(status().isOk());
        verify(exactly(1), patchRequestedFor(urlEqualTo("/hardship/" + HARDSHIP_ID)));
    }

    private void stubForOAuth() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> token = Map.of(
                "expires_in", 3600,
                "token_type", "Bearer",
                "access_token", java.util.UUID.randomUUID()
        );

        wiremock.stubFor(
                post("/oauth2/token").willReturn(
                        WireMock.ok()
                                .withHeader("Content-Type", String.valueOf(APPLICATION_JSON))
                                .withBody(mapper.writeValueAsString(token))
                )
        );
    }

    @Test
    void givenAEmptyContent_whenCalculateHardshipIsInvoked_thenFailsWithBadRequest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(ENDPOINT_URL_CALC_HARDSHIP).contentType(APPLICATION_JSON).content("{}")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAEmptyOAuthToken_whenCalculateHardshipIsInvoked_thenFailsWithUnauthorizedAccess() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(ENDPOINT_URL_CALC_HARDSHIP).contentType(APPLICATION_JSON).content("{}"))
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
                        .withHeader("Content-Type", String.valueOf(APPLICATION_JSON))
                        .withBody(objectMapper.writeValueAsString(BigDecimal.TEN))));

        mvc.perform(MockMvcRequestBuilders.post(ENDPOINT_URL_CALC_HARDSHIP).contentType(APPLICATION_JSON).content(requestBody)
                        .header(HttpHeaders.AUTHORIZATION, BEARER_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON)).andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.postHardshipDisposableIncome").value(-2380.0));
        verify(exactly(1), getRequestedFor(urlEqualTo("/fullAssessmentThreshold/2022-12-14")));
    }

}