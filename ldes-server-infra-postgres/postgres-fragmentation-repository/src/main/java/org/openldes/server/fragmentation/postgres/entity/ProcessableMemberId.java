package org.openldes.server.fragmentation.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class ProcessableMemberId implements Serializable {
	@Column(name = "member_id", nullable = false, columnDefinition = "BIGINT")
	private Long memberId;

	@Column(name = "view_id", nullable = false)
	private Integer viewId;

	public ProcessableMemberId() {
	}

	public ProcessableMemberId(Long memberId, Integer viewId) {
		this.memberId = memberId;
		this.viewId = viewId;
	}

	public Long getMemberId() {
		return memberId;
	}

	public Integer getViewId() {
		return viewId;
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ProcessableMemberId that)) return false;

		return memberId.equals(that.memberId) && viewId.equals(that.viewId);
	}

	@Override
	public int hashCode() {
		int result = memberId.hashCode();
		result = 31 * result + viewId.hashCode();
		return result;
	}
}
