package org.openldes.server.ingest.validation;

import org.openldes.server.ingest.entities.IngestedMember;

public interface MemberIngestValidator {

	void validate(IngestedMember member);

}
