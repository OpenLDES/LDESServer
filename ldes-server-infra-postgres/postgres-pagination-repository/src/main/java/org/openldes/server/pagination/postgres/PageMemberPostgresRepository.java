package org.openldes.server.pagination.postgres;

import java.util.List;
import org.openldes.server.pagination.entities.Page;
import org.openldes.server.pagination.postgres.entity.PageEntity;
import org.openldes.server.pagination.postgres.repository.PageMemberEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public class PageMemberPostgresRepository implements org.openldes.server.pagination.repositories.PageMemberRepository {

    private final PageMemberEntityRepository entityRepository;

	public PageMemberPostgresRepository(PageMemberEntityRepository entityRepository) {
        this.entityRepository = entityRepository;
	}

    @Override
    public List<Long> getUnpaginatedMembersForBucket(long bucketId) {
        return entityRepository.findByBucketIdAndPageIdIsNullOrderByMemberId(bucketId);
    }

    @Override
    public void assignMembersToPage(Page openPage, List<Long> pageMembers) {
        entityRepository.updatePageForMembers(new PageEntity(openPage.getId()), openPage.getBucketId(), pageMembers);
    }
}
