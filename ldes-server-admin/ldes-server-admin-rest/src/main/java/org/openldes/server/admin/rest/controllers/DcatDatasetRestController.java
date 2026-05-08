package org.openldes.server.admin.rest.controllers;

import static org.apache.jena.riot.WebContent.contentTypeJSONLD;
import static org.apache.jena.riot.WebContent.contentTypeNQuads;
import static org.apache.jena.riot.WebContent.contentTypeTurtle;
import static org.openldes.server.admin.rest.controllers.DcatDatasetRestController.BASE_URL;

import io.micrometer.observation.annotation.Observed;
import org.apache.jena.rdf.model.Model;
import org.openldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import org.openldes.server.admin.domain.dcat.dcatdataset.services.DcatDatasetService;
import org.openldes.server.admin.domain.validation.dcat.DcatDatasetValidator;
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
@RequestMapping(value = BASE_URL)
public class DcatDatasetRestController implements OpenApiDcatDatasetController {
	public static final String BASE_URL = "/admin/api/v1/eventstreams/{collectionName}/dcat";
	private final DcatDatasetService datasetService;
	private final DcatDatasetValidator validator;

	public DcatDatasetRestController(DcatDatasetService datasetService, DcatDatasetValidator validator) {
		this.datasetService = datasetService;
		this.validator = validator;

	}

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}

	@PostMapping(consumes = { contentTypeJSONLD, contentTypeNQuads, contentTypeTurtle })
	@ResponseStatus(HttpStatus.CREATED)
	public void postDataset(@PathVariable String collectionName, @RequestBody @Validated Model datasetModel) {
		datasetService.saveDataset(new DcatDataset(collectionName, datasetModel));
	}

	@PutMapping(consumes = { contentTypeJSONLD, contentTypeNQuads, contentTypeTurtle })
	@ResponseStatus(HttpStatus.OK)
	public void putDataset(@PathVariable String collectionName, @RequestBody @Validated Model datasetModel) {
		datasetService.updateDataset(new DcatDataset(collectionName, datasetModel));
	}

	@DeleteMapping
	@ResponseStatus(HttpStatus.OK)
	public void deleteDataset(@PathVariable String collectionName) {
		datasetService.deleteDataset(collectionName);
	}
}
