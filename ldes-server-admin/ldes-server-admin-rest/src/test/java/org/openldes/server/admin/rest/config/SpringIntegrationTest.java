package org.openldes.server.admin.rest.config;

import org.openldes.server.admin.domain.dcat.dcatdataset.repository.DcatDatasetRepository;
import org.openldes.server.admin.domain.dcat.dcatserver.repository.DcatServerRepository;
import org.openldes.server.admin.domain.eventsource.repository.EventSourceRepository;
import org.openldes.server.admin.domain.eventsource.services.EventSourceServiceImpl;
import org.openldes.server.admin.domain.eventstream.repository.EventStreamRepository;
import org.openldes.server.admin.domain.kafkasource.KafkaSourceRepository;
import org.openldes.server.admin.domain.shacl.repository.ShaclShapeRepository;
import org.openldes.server.admin.domain.view.repository.DcatViewRepository;
import org.openldes.server.admin.domain.view.repository.ViewRepository;
import org.openldes.server.admin.rest.controllers.*;
import org.openldes.server.domain.converter.PrefixAdderImpl;
import org.openldes.server.domain.converter.RdfModelConverter;
import org.openldes.server.domain.rest.HostNamePrefixConstructorConfig;
import org.openldes.server.domain.rest.RelativeUriPrefixConstructor;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@CucumberContextConfiguration
@EnableAutoConfiguration
@ActiveProfiles("test")
@ContextConfiguration(classes = { AdminEventStreamsRestController.class, AdminViewsRestController.class, AdminServerDcatController.class,
		DcatViewsRestController.class, DcatDatasetRestController.class, EventSourceServiceImpl.class, PrefixAdderImpl.class,
		HostNamePrefixConstructorConfig.class, RelativeUriPrefixConstructor.class, RdfModelConverter.class})
@ComponentScan(value = {
		"org.openldes.server.admin.spi",
		"org.openldes.server.admin.domain.eventstream",
		"org.openldes.server.admin.domain.eventstream.services",
		"org.openldes.server.admin.domain.view",
		"org.openldes.server.admin.domain.shacl",
		"org.openldes.server.admin.domain.dcat.dcatserver",
		"org.openldes.server.admin.domain.dcat.dcatdataset",
		"org.openldes.server.admin.domain.validation",
		"org.openldes.server.admin.rest.config",
		"org.openldes.server.admin.rest.converters",
		"org.openldes.server.admin.rest.exceptionhandling",
		"org.openldes.server.admin.rest.config" })
@SuppressWarnings("java:S2187")
public class SpringIntegrationTest {

	@Autowired
	@MockBean
	protected DcatDatasetRepository dcatDatasetRepository;

	@Autowired
	@MockBean
	protected DcatViewRepository dcatViewRepository;

	@Autowired
	@MockBean
	protected DcatServerRepository dcatServerRepository;

	@Autowired
	@MockBean
	protected EventSourceRepository eventSourceRepository;

	@Autowired
	@MockBean
	protected EventStreamRepository eventStreamRepository;

	@Autowired
	@MockBean
	protected ViewRepository viewRepository;

	@Autowired
	@MockBean
	protected ShaclShapeRepository shaclShapeRepository;

	@MockBean
	protected KafkaSourceRepository kafkaSourceRepository;

	@Autowired
	protected ApplicationEventPublisher eventPublisher;

	@Autowired
	protected MockMvc mockMvc;
	@Autowired
	protected ResourceRemover resourceRemover;
}
