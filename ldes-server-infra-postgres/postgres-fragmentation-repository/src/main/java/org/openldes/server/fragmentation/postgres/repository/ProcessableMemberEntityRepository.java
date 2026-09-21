package org.openldes.server.fragmentation.postgres.repository;

import java.util.List;
import org.openldes.server.fragmentation.postgres.entity.ProcessableMemberEntity;
import org.openldes.server.fragmentation.postgres.entity.ProcessableMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProcessableMemberEntityRepository extends JpaRepository<ProcessableMemberEntity, ProcessableMemberId> {
	@Query("SELECT pm.isFragmented FROM ProcessableMemberEntity pm WHERE pm.collection.name = :collectionName")
	List<Boolean> findFragmentationStatesByCollectionName(String collectionName);
}
