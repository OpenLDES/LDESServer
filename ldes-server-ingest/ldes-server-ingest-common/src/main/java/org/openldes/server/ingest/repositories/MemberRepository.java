package org.openldes.server.ingest.repositories;

import java.util.List;
import java.util.stream.Stream;
import org.openldes.server.ingest.entities.IngestedMember;

public interface MemberRepository {
	int insertAll(List<IngestedMember> members);

	Stream<IngestedMember> findAllByCollectionAndSubject(String collectionName, List<String> subjects);

	void deleteMembersByCollectionNameAndSubjects(String collectionName, List<String> subjects);
}
