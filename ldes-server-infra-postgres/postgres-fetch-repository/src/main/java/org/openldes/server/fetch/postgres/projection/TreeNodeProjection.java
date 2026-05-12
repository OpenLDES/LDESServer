package org.openldes.server.fetch.postgres.projection;

import java.time.LocalDateTime;
import java.util.List;
import org.openldes.server.fetch.postgres.entity.FetchBucketEntity;
import org.openldes.server.fetch.postgres.entity.FetchPageRelationEntity;

public interface TreeNodeProjection {
	long getId();
	String getPartialUrl();
	boolean isImmutable();
	boolean isView();
	List<FetchPageRelationEntity> getRelations();
	FetchBucketEntity getBucket();
	LocalDateTime getNextUpdateTs();
}
