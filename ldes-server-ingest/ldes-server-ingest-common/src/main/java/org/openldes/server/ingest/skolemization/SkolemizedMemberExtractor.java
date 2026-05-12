package org.openldes.server.ingest.skolemization;

import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.openldes.server.ingest.entities.IngestedMember;
import org.openldes.server.ingest.extractor.BaseMemberExtractor;
import org.openldes.server.ingest.extractor.MemberExtractor;

public class SkolemizedMemberExtractor extends BaseMemberExtractor {
	public static final String SKOLEM_URI = "/.well-known/genid/";
	private final String skolemUriTemplate;

	public SkolemizedMemberExtractor(MemberExtractor baseMemberExtractor, String skolemizationDomain) {
		super(baseMemberExtractor);
		this.skolemUriTemplate = skolemizationDomain + SKOLEM_URI + "%s";
	}

	@Override
	public List<IngestedMember> extractMembers(Model ingestedModel) {
		return super.extractMembers(ingestedModel).stream()
				.map(member -> new IngestedMember(
						member.getSubject(),
						member.getCollectionName(),
						member.getVersionOf(),
						member.getTimestamp(),
						member.isInEventSource(),
						member.getTransactionId(),
						new SkolemizedModel(skolemUriTemplate, member.getModel()).getModel()
				))
				.toList();
	}
}
