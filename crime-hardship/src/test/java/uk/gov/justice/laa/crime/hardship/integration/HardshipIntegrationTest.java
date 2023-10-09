package uk.gov.justice.laa.crime.hardship.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.justice.laa.crime.hardship.CrimeHardshipApplication;
import uk.gov.justice.laa.crime.hardship.config.CrimeHardshipTestConfiguration;
import uk.gov.justice.laa.crime.hardship.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.hardship.model.*;
import uk.gov.justice.laa.crime.hardship.model.maat_api.ApiHardshipDetail;
import uk.gov.justice.laa.crime.hardship.model.maat_api.ApiPersistHardshipResponse;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.NewWorkReason;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.gov.justice.laa.crime.hardship.data.builder.TestModelDataBuilder.DETAIL_TYPE;
import static uk.gov.justice.laa.crime.hardship.data.builder.TestModelDataBuilder.TEST_REP_ID;
import static uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewDetailType.EXPENDITURE;
import static uk.gov.justice.laa.crime.hardship.util.RequestBuilderUtils.buildRequestGivenContent;

@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(CrimeHardshipTestConfiguration.class)
@SpringBootTest(classes = CrimeHardshipApplication.class, webEnvironment = DEFINED_PORT)
class HardshipIntegrationTest {

    private MockMvc mvc;
    private static final WireMockServer wiremock = new WireMockServer(9999);
    private static final String ENDPOINT_URL = "/api/internal/v1/hardship";

    private static final String ENDPOINT_URL_CALCULATE_HARDSHIP = "/api/internal/v1/hardship/calculate-hardship-for-detail";

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

    @AfterAll
    static void clean() {
        wiremock.shutdown();
    }

    @BeforeAll
    void init() throws JsonProcessingException {
        wiremock.start();
        stubForOAuth();
    }

    @BeforeEach
    public void setup() {
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

        wiremock.stubFor(put(urlEqualTo("/api/internal/v1/assessment/hardship")).willReturn(
                WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(objectMapper.writeValueAsString(response))));

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestBody, ENDPOINT_URL)).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.hardshipReviewId").value(1000));
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

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestBody, ENDPOINT_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hardshipReviewId").value(1000));
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

        wiremock.stubFor(get(urlEqualTo("/api/internal/v1/assessment/hardship/repId/"+TEST_REP_ID+"/detailType/"+DETAIL_TYPE)).willReturn(
                WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(objectMapper.writeValueAsString(hardshipDetails))));

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestBody, ENDPOINT_URL_CALCULATE_HARDSHIP))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.hardshipSummary").value(260));
    }

    @Test
    void givenInvalidRequest_whenCalculateHardshipForDetailIsInvoked_thenFailsWithBadRequest() throws Exception {
        ApiCalculateHardshipByDetailRequest request = new ApiCalculateHardshipByDetailRequest();

        String requestBody = objectMapper.writeValueAsString(request);

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestBody, ENDPOINT_URL))
                .andExpect(status().isBadRequest());
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

}