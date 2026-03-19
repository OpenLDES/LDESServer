package org.openldes.server.admin.domain.shacl.services;

import org.openldes.server.admin.domain.shacl.entities.ShaclShape;

public interface ShaclShapeService {

	ShaclShape retrieveShaclShape(String collectionName);

	ShaclShape updateShaclShape(ShaclShape shaclShape);

	void deleteShaclShape(String collectionName);
}
