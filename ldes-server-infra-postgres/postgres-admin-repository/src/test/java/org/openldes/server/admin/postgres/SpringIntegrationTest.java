package org.openldes.server.admin.postgres;

import io.cucumber.spring.CucumberContextConfiguration;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.openldes.server.admin.domain.eventstream.repository.EventStreamRepository;
import org.openldes.server.admin.domain.view.repository.DcatViewRepository;
import org.openldes.server.admin.domain.view.repository.ViewRepository;
import org.openldes.server.admin.postgres.dcatdataservice.repository.DcatDataServiceEntityRepository;
import org.openldes.server.admin.postgres.dcatserver.DcatCatalogPostgresRepository;
import org.openldes.server.admin.postgres.dcatserver.repository.DcatCatalogEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@EnableAutoConfiguration
@DataJpaTest
@ActiveProfiles("postgres-test")
@AutoConfigureEmbeddedDatabase
@ContextConfiguration(classes = { DcatDataServiceEntityRepository.class, DcatCatalogEntityRepository.class })
@ComponentScan(value = { "org.openldes.server.admin.postgres" })
@SuppressWarnings("java:S2187")
public class SpringIntegrationTest {
	@Autowired
	public DcatCatalogPostgresRepository dcatCatalogPostgresRepository;
	@Autowired
	public DcatViewRepository dcatViewPostgresRepository;
	@Autowired
	public EventStreamRepository eventStreamRepository;
	@Autowired
	public ViewRepository viewRepository;
}
