package org.openldes.server.retention.repositories;

import org.openldes.server.domain.model.ViewName;
import org.openldes.server.retention.entities.MemberProperties;
import org.openldes.server.retention.services.retentionpolicy.definition.timeandversionbased.TimeAndVersionBasedRetentionPolicy;
import org.openldes.server.retention.services.retentionpolicy.definition.timebased.TimeBasedRetentionPolicy;
import org.openldes.server.retention.services.retentionpolicy.definition.versionbased.VersionBasedRetentionPolicy;

import java.util.List;
import java.util.stream.Stream;

public interface MemberPropertiesRepository {

	void deleteAllByIds(List<Long> id);

	void removeFromEventSource(List<Long> id);

	/**
	 * Finds all
	 *
	 * @param viewName
	 * @param policy
	 */
	List<Long> findExpiredMembers(ViewName viewName, TimeBasedRetentionPolicy policy);
	List<Long> findExpiredMembers(ViewName viewName, VersionBasedRetentionPolicy policy);
	List<Long> findExpiredMembers(ViewName viewName, TimeAndVersionBasedRetentionPolicy policy);
	Stream<MemberProperties> retrieveExpiredMembers(String collectionName, TimeBasedRetentionPolicy policy);
	Stream<MemberProperties> retrieveExpiredMembers(String collectionName, VersionBasedRetentionPolicy policy);
	Stream<MemberProperties> retrieveExpiredMembers(String collectionName, TimeAndVersionBasedRetentionPolicy policy);
}
