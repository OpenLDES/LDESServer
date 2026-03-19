package org.openldes.server.admin.domain.shacl.repository;

import org.openldes.server.admin.domain.shacl.entities.ShaclShape;

import java.util.List;
import java.util.Optional;

public interface ShaclShapeRepository {
	List<ShaclShape> retrieveAllShaclShapes();

	Optional<ShaclShape> retrieveShaclShape(String collectionName);

	void saveShaclShape(ShaclShape shaclShape);

	void deleteShaclShape(String collectionName);
}
