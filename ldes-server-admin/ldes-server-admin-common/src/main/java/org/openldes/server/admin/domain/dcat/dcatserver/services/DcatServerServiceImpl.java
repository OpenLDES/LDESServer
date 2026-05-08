package org.openldes.server.admin.domain.dcat.dcatserver.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.openldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import org.openldes.server.admin.domain.dcat.dcatdataset.services.DcatDatasetService;
import org.openldes.server.admin.domain.dcat.dcatserver.entities.DcatServer;
import org.openldes.server.admin.domain.dcat.dcatserver.repository.DcatServerRepository;
import org.openldes.server.admin.domain.validation.DcatShaclValidator;
import org.openldes.server.admin.domain.validation.ModelValidator;
import org.openldes.server.admin.domain.view.service.DcatViewService;
import org.openldes.server.domain.exceptions.ExistingResourceException;
import org.openldes.server.domain.exceptions.MissingResourceException;
import org.openldes.server.domain.model.DcatView;
import org.openldes.server.domain.rest.UriPrefixConstructor;
import org.springframework.stereotype.Service;

@Service
public class DcatServerServiceImpl implements DcatServerService {

	private final DcatServerRepository dcatServerRepository;
	private final DcatViewService dcatViewService;
	private final DcatDatasetService dcatDatasetService;
	private final UriPrefixConstructor prefixConstructor;

	private final ModelValidator dcatShaclValidator;

	public DcatServerServiceImpl(DcatServerRepository dcatServerRepository,
								 DcatViewService dcatViewService,
								 DcatDatasetService dcatDatasetService,
								 DcatShaclValidator dcatShaclValidator,
								 UriPrefixConstructor prefixConstructor) {
		this.dcatServerRepository = dcatServerRepository;
		this.dcatViewService = dcatViewService;
		this.dcatDatasetService = dcatDatasetService;
		this.prefixConstructor = prefixConstructor;
		this.dcatShaclValidator = dcatShaclValidator;
	}

	@Override
	public Model getComposedDcat() {
		final Model composedDcat = ModelFactory.createDefaultModel();

		dcatServerRepository
				.findSingleDcatServer()
				.ifPresent(dcatServer -> composedDcat.add(getStatementsForComposedDcat(dcatServer)));

		dcatShaclValidator.validate(composedDcat);
		return composedDcat;
	}

	private List<Statement> getStatementsForComposedDcat(DcatServer dcatServer) {
		String prefix = prefixConstructor.buildPrefix();
		final List<Statement> statements = new ArrayList<>();

		final List<DcatView> views = dcatViewService.findAll();
		statements.addAll(
				views.stream().flatMap(dcatView -> dcatView.getStatementsWithBase(prefix).stream()).toList());

		final List<DcatDataset> datasets = dcatDatasetService.findAll();
		statements.addAll(
				datasets.stream().map(dataset -> dataset.getModelWithIdentity(prefix))
						.flatMap(model -> model.listStatements().toList().stream()).toList());

		statements.addAll(dcatServer.getStatementsWithBase(prefix, views, datasets));

		return statements;
	}

	@Override
	public DcatServer createDcatServer(Model dcat) {
		List<DcatServer> dcatServers = dcatServerRepository.getServerDcat();
		if (!dcatServers.isEmpty()) {
			throw new ExistingResourceException("dcat-catalog", dcatServers.get(0).getId());
		}
		final DcatServer dcatServer = new DcatServer(UUID.randomUUID().toString(), dcat);
		return dcatServerRepository.saveServerDcat(dcatServer);
	}

	@Override
	public DcatServer updateDcatServer(String id, Model dcat) {
		if (dcatServerRepository.getServerDcatById(id).isEmpty()) {
			throw new MissingResourceException("dcat-catalog", id);
		}
		final DcatServer dcatServer = new DcatServer(id, dcat);
		return dcatServerRepository.saveServerDcat(dcatServer);
	}

	@Override
	public void deleteDcatServer(String id) {
		dcatServerRepository.deleteServerDcat(id);
	}

}
