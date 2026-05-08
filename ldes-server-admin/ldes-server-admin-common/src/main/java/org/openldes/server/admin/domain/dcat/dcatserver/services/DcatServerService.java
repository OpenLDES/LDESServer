package org.openldes.server.admin.domain.dcat.dcatserver.services;

import org.apache.jena.rdf.model.Model;
import org.openldes.server.admin.domain.dcat.dcatserver.entities.DcatServer;

public interface DcatServerService {

	Model getComposedDcat();

	DcatServer createDcatServer(Model dcat);

	DcatServer updateDcatServer(String id, Model dcat);

	void deleteDcatServer(String id);

}
