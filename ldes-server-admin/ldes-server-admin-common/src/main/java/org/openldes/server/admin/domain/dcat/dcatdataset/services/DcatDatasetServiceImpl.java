package org.openldes.server.admin.domain.dcat.dcatdataset.services;

import java.util.List;
import java.util.Optional;
import org.openldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import org.openldes.server.admin.domain.dcat.dcatdataset.repository.DcatDatasetRepository;
import org.openldes.server.domain.exceptions.ExistingResourceException;
import org.openldes.server.domain.exceptions.MissingResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DcatDatasetServiceImpl implements DcatDatasetService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DcatDatasetServiceImpl.class);
	private final DcatDatasetRepository repository;

	public DcatDatasetServiceImpl(DcatDatasetRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<DcatDataset> findAll() {
		return repository.findAll();
	}

	@Override
	public Optional<DcatDataset> retrieveDataset(String collectionName) {
		return repository.retrieveDataset(collectionName);
	}

	@Override
	public void saveDataset(DcatDataset dataset) {
		repository.retrieveDataset(dataset.getCollectionName()).ifPresent(d -> {
			throw new ExistingResourceException("dcat-dataset", dataset.getCollectionName());
		});
		repository.saveDataset(dataset);
	}

	@Override
	public void updateDataset(DcatDataset dataset) {
		if (repository.retrieveDataset(dataset.getCollectionName()).isEmpty()) {
			throw new MissingResourceException("dcat-dataset", dataset.getCollectionName());
		}
		repository.saveDataset(dataset);
	}

	@Override
	public void deleteDataset(String collectionName) {
		if (!repository.exitsByCollectionName(collectionName)) {
			LOGGER.warn("No metadata found for collection: {}", collectionName);
		} else {
			repository.deleteDataset(collectionName);
		}
	}

}
