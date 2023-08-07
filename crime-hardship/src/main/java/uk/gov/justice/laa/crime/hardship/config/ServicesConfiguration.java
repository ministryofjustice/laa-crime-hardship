package uk.gov.justice.laa.crime.hardship.config;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "services")
public class ServicesConfiguration {

    @NotNull
    private MaatApi maatApi;

    @NotNull
    private boolean oAuthEnabled;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MaatApi {

        @NotNull
        private String baseUrl;

        @NotNull
        private HardshipEndpoints hardshipEndpoints;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class HardshipEndpoints {

            @NotNull
            private String hardshipDetailUrl;
        }
    }
}
