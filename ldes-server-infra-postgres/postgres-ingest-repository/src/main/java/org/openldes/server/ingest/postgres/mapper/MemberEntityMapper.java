package org.openldes.server.ingest.postgres.mapper;

import org.openldes.server.ingest.entities.IngestedMember;
import org.openldes.server.ingest.postgres.entity.MemberEntity;
import org.springframework.stereotype.Component;

@Component
public class MemberEntityMapper {
	public IngestedMember toMember(MemberEntity memberEntity) {
		return new IngestedMember(
				memberEntity.getSubject(),
				memberEntity.getCollection().getName(),
				memberEntity.getVersionOf(),
				memberEntity.getTimestamp(),
				memberEntity.isInEventSource(),
				memberEntity.getTransactionId(),
				memberEntity.getModel()
		);
	}

}
