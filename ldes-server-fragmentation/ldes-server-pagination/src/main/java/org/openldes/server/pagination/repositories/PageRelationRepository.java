package org.openldes.server.pagination.repositories;

public interface PageRelationRepository {
	void insertGenericBucketRelation(long fromPageId, long toPageId);
}
