package uk.gov.justice.laa.crime.hardship;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.tracing.zipkin.ZipkinAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CrimeHardshipApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrimeHardshipApplication.class, args);
	}

}
