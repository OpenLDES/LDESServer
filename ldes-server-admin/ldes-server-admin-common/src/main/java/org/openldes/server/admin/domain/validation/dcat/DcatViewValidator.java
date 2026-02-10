package org.openldes.server.admin.domain.validation.dcat;

import org.openldes.server.admin.domain.validation.dcat.blanknodevalidators.DcatBlankNodeValidator;
import org.openldes.server.admin.domain.validation.dcat.cannotcontainvalidators.CannotContainCatalogValidator;
import org.openldes.server.admin.domain.validation.dcat.cannotcontainvalidators.CannotContainDatasetValidator;
import org.springframework.stereotype.Component;

@Component
public class DcatViewValidator extends DcatValidator {
	public DcatViewValidator() {
		super(new DcatBlankNodeValidator(DCAT_DATA_SERVICE), new CannotContainDatasetValidator(),
				new CannotContainCatalogValidator());
	}
}
