package org.openldes.server.admin.rest.controllers;

import static org.apache.jena.riot.WebContent.contentTypeJSONLD;
import static org.apache.jena.riot.WebContent.contentTypeNQuads;
import static org.apache.jena.riot.WebContent.contentTypeTurtle;

import io.micrometer.observation.annotation.Observed;
import org.apache.jena.rdf.model.Model;
import org.openldes.server.admin.domain.shacl.entities.ShaclShape;
import org.openldes.server.admin.domain.shacl.services.ShaclShapeService;
import org.openldes.server.admin.domain.validation.ModelValidator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Observed
@RestController
@RequestMapping("/admin/api/v1/eventstreams/{collectionName}/shape")
public class AdminShapeRestController implements OpenApiShapeController {
	private final ModelValidator shapeValidator;
	private final ShaclShapeService shaclShapeService;

	public AdminShapeRestController(@Qualifier("shaclShapeShaclValidator") ModelValidator shapeValidator,
									ShaclShapeService shaclShapeService) {
		this.shapeValidator = shapeValidator;
		this.shaclShapeService = shaclShapeService;
	}

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(shapeValidator);
	}

	@Override
	@GetMapping
	public Model getShape(@PathVariable String collectionName) {
		ShaclShape shape = shaclShapeService.retrieveShaclShape(collectionName);
		return shape.getModel();
	}

	@Override
	@PutMapping(consumes = {contentTypeJSONLD, contentTypeNQuads, contentTypeTurtle})
	public Model putShape(@PathVariable String collectionName, @RequestBody @Validated Model shape) {
		shaclShapeService.updateShaclShape(new ShaclShape(collectionName, shape));
		return shape;
	}

}
