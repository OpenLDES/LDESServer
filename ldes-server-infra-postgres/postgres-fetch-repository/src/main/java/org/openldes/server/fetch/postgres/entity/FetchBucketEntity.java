package org.openldes.server.fetch.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.openldes.server.admin.postgres.view.entity.ViewEntity;

@Entity
@Table(name = "buckets", indexes = @Index(unique = true, columnList = "view_id,bucket"))
public class FetchBucketEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "bucket_id", nullable = false, unique = true, columnDefinition = "BIGINT")
	private Long bucketId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "view_id", nullable = false, columnDefinition = "INT")
	private ViewEntity view;

	@Column(name = "bucket", columnDefinition = "VARCHAR(255)")
	private String bucketDescriptor;

	public FetchBucketEntity() {}

	public FetchBucketEntity(Long bucketId) {
		this.bucketId = bucketId;
	}

	public FetchBucketEntity(ViewEntity view, String bucketDescriptor) {
		this.view = view;
		this.bucketDescriptor = bucketDescriptor;
	}

	public Long getBucketId() {
		return bucketId;
	}

	public ViewEntity getView() {
		return view;
	}

	public String getBucketDescriptor() {
		return bucketDescriptor;
	}
}
