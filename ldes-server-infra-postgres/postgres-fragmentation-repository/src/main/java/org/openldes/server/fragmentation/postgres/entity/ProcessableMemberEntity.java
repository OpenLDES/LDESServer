package org.openldes.server.fragmentation.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.openldes.server.admin.postgres.eventstream.entity.EventStreamEntity;
import org.openldes.server.admin.postgres.view.entity.ViewEntity;

/**
 * Links a member to a view of its collection and keeps track of whether that member has already been
 * fragmented for that specific view. See <a href="https://github.com/OpenLDES/LDESServer/issues/48">issue 48</a>.
 */
@Entity
@Table(name = "processable_members")
public class ProcessableMemberEntity {
	@EmbeddedId
	private ProcessableMemberId processableMemberId;

	@MapsId("viewId")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "view_id", nullable = false)
	private ViewEntity view;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "collection_id", nullable = false)
	private EventStreamEntity collection;

	@Column(name = "is_fragmented", nullable = false)
	private boolean isFragmented;

	public ProcessableMemberId getProcessableMemberId() {
		return processableMemberId;
	}

	public ViewEntity getView() {
		return view;
	}

	public EventStreamEntity getCollection() {
		return collection;
	}

	public boolean isFragmented() {
		return isFragmented;
	}
}
