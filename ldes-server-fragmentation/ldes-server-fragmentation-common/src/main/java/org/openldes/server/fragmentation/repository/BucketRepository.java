package org.openldes.server.fragmentation.repository;

import org.openldes.server.domain.model.ViewName;
import org.openldes.server.fragmentation.entities.Bucket;

import java.util.Optional;

public interface BucketRepository {
	Bucket insertRootBucket(Bucket bucket);
	Optional<Bucket> retrieveRootBucket(ViewName viewName);
}
