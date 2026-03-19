package org.openldes.server.fragmentation.postgres.projections;

public interface BucketProjection {
	Long getBucketId();
	String getBucketDescriptor();
	String getViewName();
}
