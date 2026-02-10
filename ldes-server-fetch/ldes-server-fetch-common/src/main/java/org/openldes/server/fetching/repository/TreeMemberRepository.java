package org.openldes.server.fetching.repository;

import org.openldes.server.fetching.entities.Member;

import java.util.stream.Stream;

public interface TreeMemberRepository {
	Stream<Member> findAllByTreeNodeUrl(String url);
}
