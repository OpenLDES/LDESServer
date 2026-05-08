package org.openldes.server.ingest;

import io.cucumber.spring.CucumberContextConfiguration;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.openldes.server.admin.domain.eventstream.repository.EventStreamRepository;
import org.openldes.server.admin.postgres.eventstream.repository.EventStreamEntityRepository;
import org.openldes.server.ingest.metrics.IngestionMetricsService;
import org.openldes.server.ingest.postgres.MemberPostgresRepository;
import org.openldes.server.ingest.postgres.repository.MemberEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

@CucumberContextConfiguration
@EnableAutoConfiguration
@DataJpaTest
@AutoConfigureEmbeddedDatabase(type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES)
@ActiveProfiles("postgres-test")
@EntityScan(basePackages = {"org.openldes.server"})
@ComponentScan(basePackages = {"org.openldes.server.ingest",
		"org.openldes.server.domain",
		"org.openldes.server.admin.postgres.eventstream"})
@ContextConfiguration(classes = {MemberEntityRepository.class})
@EnableJpaRepositories(basePackageClasses = {MemberEntityRepository.class, EventStreamEntityRepository.class})
@Sql(value = {"init-collections.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(statements = "DELETE FROM collections;", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Import(BuildProperties.class)
@SuppressWarnings("java:S2187")
public class PostgresIngestIntegrationTest {

	@Autowired
	MemberPostgresRepository memberRepository;
	@Autowired
	EventStreamRepository eventStreamRepository;
	@MockitoBean
	IngestionMetricsService metricsService;
}
