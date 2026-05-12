package org.openldes.server.admin.domain.dcat.dcatserver.repository;

import java.util.List;
import java.util.Optional;
import org.openldes.server.admin.domain.dcat.dcatserver.entities.DcatServer;

public interface DcatServerRepository {
	List<DcatServer> getServerDcat();

	Optional<DcatServer> getServerDcatById(String id);

	DcatServer saveServerDcat(DcatServer dcatServer);

	void deleteServerDcat(String id);

	Optional<DcatServer> findSingleDcatServer();
}
