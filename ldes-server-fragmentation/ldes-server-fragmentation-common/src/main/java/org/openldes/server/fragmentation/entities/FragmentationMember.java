package org.openldes.server.fragmentation.entities;

import java.time.LocalDateTime;
import org.apache.jena.rdf.model.Model;
import org.openldes.server.domain.converter.VersionObjectModelBuilder;
import org.openldes.server.fragmentation.valueobjects.EventStreamProperties;

public final class FragmentationMember {
	private final long memberId;
	private final String subject;
	private final String versionOf;
	private final LocalDateTime timestamp;
	private final EventStreamProperties eventStreamProperties;
	private final Model model;

	public FragmentationMember(
			long memberId,
			String subject,
			String versionOf,
			LocalDateTime timestamp,
			EventStreamProperties eventStreamProperties,
			Model model
	) {
		this.memberId = memberId;
		this.subject = subject;
		this.versionOf = versionOf;
		this.timestamp = timestamp;
		this.eventStreamProperties = eventStreamProperties;
		this.model = model;
	}

	public long getMemberId() {
		return memberId;
	}

	public String getSubject() {
		return subject;
	}

	public String getCollectionName() {
		return eventStreamProperties.collectionName();
	}

	public Model getVersionModel() {
		final String subjectUri = subject.startsWith("http") ? subject : subject.substring(subject.indexOf("/") + 1);
		if(!eventStreamProperties.versionCreationEnabled()) {
			return model;
		}
		return VersionObjectModelBuilder.create()
				.withMemberSubject(subjectUri)
				.withVersionOfProperties(eventStreamProperties.versionOfPath(), versionOf)
				.withTimestampProperties(eventStreamProperties.timestampPath(), timestamp)
				.withModel(model)
				.buildVersionObjectModel();

	}
}
