package org.openldes.server.maintenance.postgres.mapper;

import org.openldes.server.maintenance.postgres.projection.RetentionMemberProjection;
import org.openldes.server.retention.entities.MemberProperties;
import org.springframework.stereotype.Component;

@Component
public class MemberPropertiesEntityMapper {

	public MemberProperties toMemberProperties(RetentionMemberProjection entity) {
		return new MemberProperties(entity.getId(),
				entity.getCollectionName(),
				entity.getVersionOf(),
				entity.getTimestamp(),
				entity.getInEventSource(),
				entity.getInView());
	}
}
