package br.com.fiap.hackathon_notification.infrastructure.config;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class FlywayConfigurationTest {

	private final FlywayConfiguration configuration = new FlywayConfiguration();

	@Test
	void shouldCreateFlywayConfiguredWithMigrationsLocation() {
		DataSource dataSource = mock(DataSource.class);

		Flyway flyway = configuration.flyway(dataSource);

		assertNotNull(flyway);
		assertNotNull(flyway.getConfiguration().getDataSource());
		assertEquals("classpath:db/migration", flyway.getConfiguration().getLocations()[0].getDescriptor());
	}
}
