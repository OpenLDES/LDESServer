package org.openldes.server.admin.domain.dcat.dcatdataset.services;

import java.util.List;
import java.util.Optional;
import org.openldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;

public interface DcatDatasetService {

	List<DcatDataset> findAll();

	Optional<DcatDataset> retrieveDataset(String collectionName);

	void saveDataset(DcatDataset dataset);

	void updateDataset(DcatDataset dataset);

	void deleteDataset(String collectionName);
}
