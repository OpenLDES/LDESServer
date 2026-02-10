package org.openldes.server.maintenance.postgres.mapper;

import org.openldes.server.compaction.domain.entities.CompactionCandidate;
import org.openldes.server.maintenance.postgres.projection.CompactionCandidateProjection;

public class CompactionCandidateMapper {
	private CompactionCandidateMapper() {}

	public static CompactionCandidate fromProjection(CompactionCandidateProjection projection) {
		return new CompactionCandidate(
				projection.getFragmentId(),
				projection.getSize(),
				projection.getToPage(),
				projection.getBucketId(),
				projection.getPartialUrl());
	}
}
