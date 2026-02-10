package org.openldes.server.admin.domain.validation.dcat;

import org.openldes.server.admin.domain.validation.dcat.blanknodevalidators.DcatBlankNodeValidator;
import org.openldes.server.admin.domain.validation.dcat.cannotcontainvalidators.CannotContainDatasetValidator;
import org.openldes.server.admin.domain.validation.dcat.cannotcontainvalidators.CannotContainServiceValidator;
import org.springframework.stereotype.Component;

@Component
public class DcatCatalogValidator extends DcatValidator {
	public DcatCatalogValidator() {
		super(new DcatBlankNodeValidator(DCAT_CATALOG), new CannotContainDatasetValidator(),
				new CannotContainServiceValidator());
	}
}
