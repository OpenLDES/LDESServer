package org.openldes.server.maintenance.postgres.projection;

public interface CompactionCandidateProjection {
	Long getFragmentId();
	Integer getSize();
	Long getToPage();
	Long getBucketId();
	String getPartialUrl();
}
