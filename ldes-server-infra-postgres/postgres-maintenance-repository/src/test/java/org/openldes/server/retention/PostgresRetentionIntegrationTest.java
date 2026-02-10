package org.openldes.server.retention;

import org.openldes.server.maintenance.postgres.MemberPropertiesPostgresRepository;
import io.cucumber.spring.CucumberContextConfiguration;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@EnableAutoConfiguration
@DataJpaTest
@AutoConfigureEmbeddedDatabase
@ActiveProfiles("postgres-test")
@ContextConfiguration(classes = {})
@ComponentScan(value = { "org.openldes.server.retention" })
@SuppressWarnings("java:S2187")
public class PostgresRetentionIntegrationTest {

	@Autowired
	MemberPropertiesPostgresRepository memberPropertiesRepository;

}
