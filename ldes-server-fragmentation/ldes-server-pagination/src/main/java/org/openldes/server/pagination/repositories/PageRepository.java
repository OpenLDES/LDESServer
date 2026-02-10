package org.openldes.server.pagination.repositories;

import org.openldes.server.pagination.entities.Page;

public interface PageRepository {
	Page getOpenPage(long bucketId);
	Page createNextPage(Page parentPage);
    void markAllPagesImmutableByCollectionName(String collectionName);
}
