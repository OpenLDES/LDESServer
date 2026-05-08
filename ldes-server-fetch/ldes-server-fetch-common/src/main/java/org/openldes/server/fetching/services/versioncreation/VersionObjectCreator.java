package org.openldes.server.fetching.services.versioncreation;

import java.time.LocalDateTime;
import org.apache.jena.rdf.model.Model;

@FunctionalInterface
public interface VersionObjectCreator {
	Model createFromMember(String subject, Model model, String versionOf, LocalDateTime timestamp);
}
