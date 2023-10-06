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
import uk.gov.justice.laa.crime.hardship.model.ApiPerformHardshipRequest;
import uk.gov.justice.laa.crime.hardship.model.HardshipMetadata;
import uk.gov.justice.laa.crime.hardship.model.HardshipReview;
import uk.gov.justice.laa.crime.hardship.model.SolicitorCosts;
import uk.gov.justice.laa.crime.hardship.model.maat_api.ApiPersistHardshipResponse;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.NewWorkReason;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.justice.laa.crime.hardship.util.RequestBuilderUtils.buildRequestGivenContent;

@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(CrimeHardshipTestConfiguration.class)
@SpringBootTest(classes = CrimeHardshipApplication.class, webEnvironment = DEFINED_PORT)
class HardshipIntegrationTest {

    private MockMvc mvc;
    private static final WireMockServer wiremock = new WireMockServer(9999);
    private static final String ENDPOINT_URL = "/api/internal/v1/hardship";

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
    void init() {
        wiremock.start();
    }

    @BeforeEach
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .addFilter(springSecurityFilterChain).build();
    }

    @Test
    void givenAEmptyContent_whenUpdateHardshipIsInvoked_thenFailsWithBadRequest() throws Exception {
        stubForOAuth();
        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, "{}", ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAEmptyOAuthToken_whenUpdateHardshipIsInvoked_thenFailsWithUnauthorizedAccess() throws Exception {
        stubForOAuth();
        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, "{}", ENDPOINT_URL, false))
                .andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    void givenAEmptyContent_whenCreateHardshipIsInvoked_thenFailsWithBadRequest() throws Exception {
        stubForOAuth();
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, "{}", ENDPOINT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAEmptyOAuthToken_whenCreateHardshipIsInvoked_thenFailsWithUnauthorizedAccess() throws Exception {
        stubForOAuth();
        mvc.perform(buildRequestGivenContent(HttpMethod.POST, "{}", ENDPOINT_URL, false))
                .andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    void givenValidRequest_whenHardshipUpdateIsInvoked_thenOkResponse() throws Exception {
        stubForOAuth();

        ApiPerformHardshipRequest request = TestModelDataBuilder.getApiPerformHardshipRequest();

        String requestBody = objectMapper.writeValueAsString(request);

        ApiPersistHardshipResponse response = TestModelDataBuilder.getApiPersistHardshipResponse();

        wiremock.stubFor(put(urlEqualTo("/api/internal/v1/assessment/hardship")).willReturn(
                WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(objectMapper.writeValueAsString(response))));

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestBody, ENDPOINT_URL)).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Test
    void givenInvalidRequest_whenHardshipUpdateIsInvoked_thenOkResponse() throws Exception {
        stubForOAuth();

        ApiPerformHardshipRequest request = new ApiPerformHardshipRequest(new HardshipReview().
                withSolicitorCosts(new SolicitorCosts().withRate(BigDecimal.ONE).withHours(null)),
                new HardshipMetadata().withReviewReason(NewWorkReason.NEW));

        String requestBody = objectMapper.writeValueAsString(request);

        ApiPersistHardshipResponse response = TestModelDataBuilder.getApiPersistHardshipResponse();

        wiremock.stubFor(put(urlEqualTo("/api/internal/v1/assessment/hardship")).willReturn(
                WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(objectMapper.writeValueAsString(response))));

        mvc.perform(buildRequestGivenContent(HttpMethod.PUT, requestBody, ENDPOINT_URL)).andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Test
    void givenValidRequest_whenHardshipCreateIsInvoked_thenOkResponse() throws Exception {
        stubForOAuth();

        ApiPerformHardshipRequest request = TestModelDataBuilder.getApiPerformHardshipRequest();

        String requestBody = objectMapper.writeValueAsString(request);

        ApiPersistHardshipResponse response = TestModelDataBuilder.getApiPersistHardshipResponse();

        wiremock.stubFor(post(urlEqualTo("/api/internal/v1/assessment/hardship")).willReturn(
                WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(objectMapper.writeValueAsString(response))));

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestBody, ENDPOINT_URL)).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Test
    void givenInvalidRequest_whenHardshipCreateIsInvoked_thenFailsWithBadRequest() throws Exception {
        stubForOAuth();

        ApiPerformHardshipRequest request = new ApiPerformHardshipRequest(new HardshipReview().
                withSolicitorCosts(new SolicitorCosts().withRate(BigDecimal.ONE).withHours(null)),
                new HardshipMetadata().withReviewReason(NewWorkReason.NEW));

        String requestBody = objectMapper.writeValueAsString(request);

        ApiPersistHardshipResponse response = TestModelDataBuilder.getApiPersistHardshipResponse();

        wiremock.stubFor(post(urlEqualTo("/api/internal/v1/assessment/hardship")).willReturn(
                WireMock.ok()
                        .withHeader("Content-Type", String.valueOf(MediaType.APPLICATION_JSON))
                        .withBody(objectMapper.writeValueAsString(response))));

        mvc.perform(buildRequestGivenContent(HttpMethod.POST, requestBody, ENDPOINT_URL)).andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
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