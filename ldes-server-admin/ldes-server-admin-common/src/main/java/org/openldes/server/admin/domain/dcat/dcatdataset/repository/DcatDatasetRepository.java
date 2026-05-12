package org.openldes.server.admin.domain.dcat.dcatdataset.repository;

import java.util.List;
import java.util.Optional;
import org.openldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;

public interface DcatDatasetRepository {

	Optional<DcatDataset> retrieveDataset(String collectionName);

	void saveDataset(DcatDataset dataset);

	void deleteDataset(String id);

	List<DcatDataset> findAll();

	boolean exitsByCollectionName(String collectionName);
}
