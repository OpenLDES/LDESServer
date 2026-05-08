package org.openldes.server.admin.rest.controllers;

import static org.apache.jena.riot.WebContent.contentTypeJSONLD;
import static org.apache.jena.riot.WebContent.contentTypeNQuads;
import static org.apache.jena.riot.WebContent.contentTypeTurtle;

import io.micrometer.observation.annotation.Observed;
import org.apache.jena.rdf.model.Model;
import org.openldes.server.admin.domain.validation.dcat.DcatViewValidator;
import org.openldes.server.admin.domain.view.service.DcatViewService;
import org.openldes.server.domain.model.ViewName;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Observed
@RestController
@RequestMapping(DcatViewsRestController.BASE_URL)
public class DcatViewsRestController implements OpenApiDcatViewsController {

	public static final String BASE_URL = "/admin/api/v1/eventstreams/{collectionName}/views/{viewName}/dcat";

	private final DcatViewService dcatViewService;

	private final DcatViewValidator dcatViewValidator;

	public DcatViewsRestController(DcatViewService dcatViewService, DcatViewValidator dcatViewValidator) {
		this.dcatViewService = dcatViewService;
		this.dcatViewValidator = dcatViewValidator;
	}

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(dcatViewValidator);
	}

	@PostMapping(consumes = { contentTypeJSONLD, contentTypeNQuads, contentTypeTurtle })
	@ResponseStatus(HttpStatus.CREATED)
	public void createDcat(@PathVariable String collectionName,
			@PathVariable String viewName,
			@RequestBody @Validated Model dcat) {
		dcatViewService.create(new ViewName(collectionName, viewName), dcat);
	}

	@PutMapping(consumes = { contentTypeJSONLD, contentTypeNQuads, contentTypeTurtle })
	public void updateDcat(@PathVariable String collectionName, @PathVariable String viewName,
			@RequestBody @Validated Model dcat) {
		dcatViewService.update(new ViewName(collectionName, viewName), dcat);
	}

	@DeleteMapping
	public void deleteDcat(@PathVariable String collectionName, @PathVariable String viewName) {
		dcatViewService.delete(new ViewName(collectionName, viewName));
	}

}
