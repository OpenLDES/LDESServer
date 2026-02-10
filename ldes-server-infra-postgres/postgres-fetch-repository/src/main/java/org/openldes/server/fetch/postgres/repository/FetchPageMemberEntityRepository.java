package org.openldes.server.fetch.postgres.repository;

import org.openldes.server.fetch.postgres.entity.FetchMemberEntity;
import org.openldes.server.fetch.postgres.entity.FetchPageMemberEntity;
import org.openldes.server.fetch.postgres.entity.PageMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FetchPageMemberEntityRepository extends JpaRepository<FetchPageMemberEntity, PageMemberId> {
	@Query("SELECT member from FetchPageMemberEntity WHERE pageId = :pageId")
	List<FetchMemberEntity> findAllMembersByPageId(Long pageId);
}
