package org.openldes.server.fragmentation.repository;

import java.util.Optional;
import org.openldes.server.domain.model.ViewName;
import org.openldes.server.fragmentation.entities.Bucket;

public interface BucketRepository {
	Bucket insertRootBucket(Bucket bucket);
	Optional<Bucket> retrieveRootBucket(ViewName viewName);
}
