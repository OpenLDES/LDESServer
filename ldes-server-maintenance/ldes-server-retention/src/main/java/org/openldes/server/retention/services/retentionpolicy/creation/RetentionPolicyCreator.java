package org.openldes.server.retention.services.retentionpolicy.creation;

import org.openldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;
import org.apache.jena.rdf.model.Model;

public interface RetentionPolicyCreator {
	RetentionPolicy createRetentionPolicy(Model model);
}
