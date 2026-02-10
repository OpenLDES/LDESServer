package org.openldes.server.fragmentation.factory;

import org.openldes.server.domain.model.ViewName;
import org.openldes.server.fragmentation.entities.Bucket;
import org.openldes.server.fragmentation.repository.BucketRepository;
import org.springframework.stereotype.Component;

@Component
public class RootBucketCreatorImpl implements RootBucketCreator {
	private final BucketRepository bucketRepository;

	public RootBucketCreatorImpl(BucketRepository bucketRepository) {
		this.bucketRepository = bucketRepository;
	}

	@Override
	public void createRootBucketForView(ViewName viewName) {
		if (bucketRepository.retrieveRootBucket(viewName).isEmpty()) {
			bucketRepository.insertRootBucket(Bucket.createRootBucketForView(viewName));
		}
	}
}
