package org.openldes.server.fetching.repository;

import java.util.stream.Stream;
import org.openldes.server.fetching.entities.Member;

public interface TreeMemberRepository {
	Stream<Member> findAllByTreeNodeUrl(String url);
}
