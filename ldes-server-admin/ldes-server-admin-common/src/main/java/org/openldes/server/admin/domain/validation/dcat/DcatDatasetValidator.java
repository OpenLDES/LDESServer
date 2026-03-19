package org.openldes.server.admin.domain.validation.dcat;

import org.openldes.server.admin.domain.validation.dcat.blanknodevalidators.DcatBlankNodeValidator;
import org.openldes.server.admin.domain.validation.dcat.cannotcontainvalidators.CannotContainCatalogValidator;
import org.openldes.server.admin.domain.validation.dcat.cannotcontainvalidators.CannotContainServiceValidator;
import org.springframework.stereotype.Component;

@Component
public class DcatDatasetValidator extends DcatValidator {
	public DcatDatasetValidator() {
		super(new DcatBlankNodeValidator(DCAT_DATASET), new CannotContainCatalogValidator(),
				new CannotContainServiceValidator());
	}
}
