package org.openldes.server.admin.domain.dcat.dcatserver.repository;

import org.openldes.server.admin.domain.dcat.dcatserver.entities.DcatServer;

import java.util.List;
import java.util.Optional;

public interface DcatServerRepository {
	List<DcatServer> getServerDcat();

	Optional<DcatServer> getServerDcatById(String id);

	DcatServer saveServerDcat(DcatServer dcatServer);

	void deleteServerDcat(String id);

	Optional<DcatServer> findSingleDcatServer();
}
