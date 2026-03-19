package org.openldes.server.fragmentation.valueobjects;

public record EventStreamProperties(
		String collectionName,
		String versionOfPath,
		String timestampPath,
		boolean versionCreationEnabled
) {
}
