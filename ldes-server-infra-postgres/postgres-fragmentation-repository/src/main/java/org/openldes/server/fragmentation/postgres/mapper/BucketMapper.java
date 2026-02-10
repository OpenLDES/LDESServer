package org.openldes.server.fragmentation.postgres.mapper;

import org.openldes.server.domain.model.ViewName;
import org.openldes.server.fragmentation.entities.Bucket;
import org.openldes.server.fragmentation.postgres.projections.BucketProjection;
import org.openldes.server.fragmentation.valueobjects.BucketDescriptor;

public class BucketMapper {
	private BucketMapper() {
	}

	public static Bucket fromProjection(BucketProjection projection) {
		return new Bucket(
				BucketDescriptor.fromString(projection.getBucketDescriptor()),
				ViewName.fromString(projection.getViewName())
		);
	}

}
