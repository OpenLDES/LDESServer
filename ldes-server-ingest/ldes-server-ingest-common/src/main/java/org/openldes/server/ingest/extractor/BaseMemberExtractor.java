package org.openldes.server.ingest.extractor;

import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.openldes.server.ingest.entities.IngestedMember;

public abstract class BaseMemberExtractor implements MemberExtractor {
	private final MemberExtractor memberExtractor;

	protected BaseMemberExtractor(MemberExtractor memberExtractor) {
		this.memberExtractor = memberExtractor;
	}

	@Override
	public List<IngestedMember> extractMembers(Model ingestedModel) {
		return memberExtractor.extractMembers(ingestedModel);
	}
}
