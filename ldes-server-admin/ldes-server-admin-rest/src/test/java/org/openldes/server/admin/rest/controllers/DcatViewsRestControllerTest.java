package org.openldes.server.admin.rest.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.openldes.server.admin.rest.controllers.DcatViewsRestController.BASE_URL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openldes.server.admin.domain.validation.dcat.DcatViewValidator;
import org.openldes.server.admin.domain.view.service.DcatViewService;
import org.openldes.server.admin.rest.IsIsomorphic;
import org.openldes.server.admin.rest.exceptionhandling.AdminRestResponseEntityExceptionHandler;
import org.openldes.server.domain.converter.HttpModelConverter;
import org.openldes.server.domain.converter.PrefixAdderImpl;
import org.openldes.server.domain.converter.RdfModelConverter;
import org.openldes.server.domain.exceptions.MissingResourceException;
import org.openldes.server.domain.model.ViewName;
import org.openldes.server.domain.rest.HostNamePrefixConstructorConfig;
import org.openldes.server.domain.rest.RelativeUriPrefixConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@ActiveProfiles({"test", "rest"})
@ContextConfiguration(classes = {DcatViewsRestController.class, PrefixAdderImpl.class,
		HttpModelConverter.class, AdminRestResponseEntityExceptionHandler.class, RdfModelConverter.class,
		HostNamePrefixConstructorConfig.class, RelativeUriPrefixConstructor.class})
class DcatViewsRestControllerTest {

	private static final String COLLECTION_NAME = "collectionName";
	private static final String VIEW_NAME = "viewName";

	@MockitoBean
	private DcatViewService dcatViewService;

	@MockitoBean
	private DcatViewValidator validator;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		when(validator.supports(any())).thenReturn(true);
	}

	@Nested
	class CreateDcat {

		@Test
		void should_Return400_when_ValidatorThrowsIllegalArgumentException() throws Exception {
			doThrow(IllegalArgumentException.class).when(validator).validate(any(), any());

			mockMvc.perform(post(BASE_URL, COLLECTION_NAME, VIEW_NAME)
							.content(writeToTurtle(readTurtleFromFile("dcat/dataservice/dcat-view-valid.ttl")))
							.contentType(Lang.TURTLE.getHeaderString()))
					.andExpect(status().isBadRequest());

			verifyNoInteractions(dcatViewService);
		}

		@Test
		void should_Return201_when_CreatedSuccessfully() throws Exception {
			Model dcat = readTurtleFromFile("dcat/dataservice/dcat-view-valid.ttl");
			mockMvc.perform(post(BASE_URL, COLLECTION_NAME, VIEW_NAME)
							.content(writeToTurtle(dcat))
							.contentType(Lang.TURTLE.getHeaderString()))
					.andExpect(status().isCreated());

			verify(dcatViewService)
					.create(eq(new ViewName(COLLECTION_NAME, VIEW_NAME)), argThat(IsIsomorphic.with(dcat)));
		}

	}

	@Nested
	class UpdateDcat {

		@Test
		void should_Return400_when_ValidatorThrowsIllegalArgumentException() throws Exception {
			doThrow(IllegalArgumentException.class).when(validator).validate(any(), any());

			mockMvc.perform(put(BASE_URL, COLLECTION_NAME, VIEW_NAME)
							.content(writeToTurtle(readTurtleFromFile("dcat/dataservice/dcat-view-valid.ttl")))
							.contentType(Lang.TURTLE.getHeaderString()))
					.andExpect(status().isBadRequest());

			verifyNoInteractions(dcatViewService);
		}

		@Test
		void should_Return200_when_UpdatedSuccessfully() throws Exception {
			Model dcat = readTurtleFromFile("dcat/dataservice/dcat-view-valid.ttl");
			mockMvc.perform(put(BASE_URL, COLLECTION_NAME, VIEW_NAME)
							.content(writeToTurtle(dcat))
							.contentType(Lang.TURTLE.getHeaderString()))
					.andExpect(status().isOk());

			verify(dcatViewService)
					.update(eq(new ViewName(COLLECTION_NAME, VIEW_NAME)), argThat(IsIsomorphic.with(dcat)));
		}

		@Test
		void should_Return404_when_ResourceNotFound() throws Exception {
			doThrow(MissingResourceException.class).when(dcatViewService).update(any(), any());

			Model dcat = readTurtleFromFile("dcat/dataservice/dcat-view-valid.ttl");
			mockMvc.perform(put(BASE_URL, COLLECTION_NAME, VIEW_NAME)
							.content(writeToTurtle(dcat))
							.contentType(Lang.TURTLE.getHeaderString()))
					.andExpect(status().isNotFound());

			verify(dcatViewService)
					.update(eq(new ViewName(COLLECTION_NAME, VIEW_NAME)), argThat(IsIsomorphic.with(dcat)));
		}
	}

	@Test
	void should_Return200_when_DeletedSuccessfully() throws Exception {
		mockMvc.perform(delete(BASE_URL, COLLECTION_NAME, VIEW_NAME))
				.andExpect(status().isOk());

		verify(dcatViewService).delete(new ViewName(COLLECTION_NAME, VIEW_NAME));
	}

	private Model readTurtleFromFile(String path) {
		return RDFParser.source(path).lang(Lang.TURTLE).build().toModel();
	}

	private String writeToTurtle(Model model) {
		return RDFWriter.source(model).lang(Lang.TURTLE).asString();
	}

}
