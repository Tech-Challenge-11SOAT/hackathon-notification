package br.com.fiap.hackathon_notification;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

class HackathonNotificationApplicationTests {

	@Test
	void shouldHaveExpectedBootAnnotations() {
		boolean hasSpringBootApplication = HackathonNotificationApplication.class
				.isAnnotationPresent(SpringBootApplication.class);
		boolean hasConfigurationPropertiesScan = HackathonNotificationApplication.class
				.isAnnotationPresent(ConfigurationPropertiesScan.class);

		org.junit.jupiter.api.Assertions.assertTrue(hasSpringBootApplication);
		org.junit.jupiter.api.Assertions.assertTrue(hasConfigurationPropertiesScan);
	}

}
