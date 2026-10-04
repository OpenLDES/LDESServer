package org.openldes.server.pagination.postgres.repository;

import org.openldes.server.pagination.postgres.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FragmentationMemberEntityRepository extends JpaRepository<MemberEntity, Long> {
}
