package org.openldes.server.admin.rest.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;
import org.openldes.server.admin.domain.dcat.dcatserver.services.DcatServerService;
import org.openldes.server.domain.exceptions.ShaclValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@EnableAutoConfiguration
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("health-test")
@ContextConfiguration(classes = {DcatHealthIndicator.class})
class DcatHealthIndicatorTest {
	@MockitoBean
	private DcatServerService dcatServerService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void given_ValidDcatConfig_when_GetDcatHealth_then_ReturnStatus200() throws Exception {
		when(dcatServerService.getComposedDcat()).thenReturn(ModelFactory.createDefaultModel());

		mockMvc.perform(get("/actuator/health/dcat-validity").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value(Status.UP.getCode()));
	}

	@Test
	void given_InvalidDcatConfig_when_GetDcatHealth_then_ReturnStatus500() throws Exception {
		when(dcatServerService.getComposedDcat()).thenThrow(ShaclValidationException.class);

		mockMvc.perform(get("/actuator/health/dcat-validity").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.status").value(Status.UNKNOWN.getCode()))
				.andExpect(jsonPath("$.components.dcat.status").value("INVALID"))
				.andExpect(jsonPath("$.components.dcat.details.error").value(new ShaclValidationException(null, null).toString()));
	}

}
