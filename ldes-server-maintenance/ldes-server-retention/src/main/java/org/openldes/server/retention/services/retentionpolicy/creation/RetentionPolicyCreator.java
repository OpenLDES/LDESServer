package org.openldes.server.retention.services.retentionpolicy.creation;

import org.apache.jena.rdf.model.Model;
import org.openldes.server.retention.services.retentionpolicy.definition.RetentionPolicy;

public interface RetentionPolicyCreator {
	RetentionPolicy createRetentionPolicy(Model model);
}
