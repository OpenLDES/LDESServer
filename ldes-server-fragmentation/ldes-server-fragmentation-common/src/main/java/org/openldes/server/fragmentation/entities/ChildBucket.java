package org.openldes.server.fragmentation.entities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.openldes.server.domain.model.ViewName;
import org.openldes.server.fragmentation.valueobjects.BucketDescriptor;
import org.openldes.server.fragmentation.valueobjects.TreeRelation;

public class ChildBucket extends Bucket {
	private final Set<TreeRelation> relations;

	public ChildBucket(long bucketId, BucketDescriptor bucketDescriptor, ViewName viewName, List<ChildBucket> children, long assignedMemberId, Set<TreeRelation> relations) {
		super(bucketId, bucketDescriptor, viewName, children, assignedMemberId);
		this.relations = new HashSet<>(relations);
	}

	public Set<TreeRelation> getRelations() {
		return relations;
	}

	public void addRelations(Set<TreeRelation> relations) {
		this.relations.addAll(relations);
	}
}
