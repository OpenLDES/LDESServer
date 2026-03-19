package org.openldes.server.admin.postgres.dcatserver.repository;

import org.openldes.server.admin.postgres.dcatserver.entity.DcatCatalogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DcatCatalogEntityRepository extends JpaRepository<DcatCatalogEntity, String> {
}
