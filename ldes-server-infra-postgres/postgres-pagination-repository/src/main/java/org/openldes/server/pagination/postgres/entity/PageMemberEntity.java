package org.openldes.server.pagination.postgres.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.openldes.server.fragmentation.postgres.entity.BucketEntity;

@Entity
@Table(name = "page_members")
public class PageMemberEntity {
	@EmbeddedId
	private PageMemberId pageMemberId;

	@MapsId("memberId")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "member_id", nullable = false, columnDefinition = "BIGINT")
	private MemberEntity member;

	@MapsId("bucketId")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "bucket_id", nullable = false, columnDefinition = "BIGINT")
	private BucketEntity bucket;

	@ManyToOne(fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "page_id", columnDefinition = "BIGINT")
	private PageEntity page;

	public void setPage(PageEntity page) {
		this.page = page;
	}

	public void setMember(Long memberId) {
		this.member = new MemberEntity(memberId);
	}

	public MemberEntity getMember() {
		return member;
	}

	public BucketEntity getBucket() {
		return bucket;
	}

	public PageEntity getPage() {
		return page;
	}
}
