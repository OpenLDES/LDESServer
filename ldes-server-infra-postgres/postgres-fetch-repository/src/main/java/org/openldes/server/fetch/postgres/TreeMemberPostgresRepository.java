package org.openldes.server.fetch.postgres;

import java.util.stream.Stream;
import org.openldes.server.fetch.postgres.mapper.MemberRowMapper;
import org.openldes.server.fetching.entities.Member;
import org.openldes.server.fetching.repository.TreeMemberRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class TreeMemberPostgresRepository implements TreeMemberRepository {

	private final JdbcTemplate jdbcTemplate;

	public TreeMemberPostgresRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public Stream<Member> findAllByTreeNodeUrl(String url) {
		String sql = """
				SELECT m.subject, m.member_model
				FROM members m
				    JOIN page_members USING (member_id)
				    JOIN pages p USING (page_id)
				WHERE p.partial_url = ?""";
		return jdbcTemplate.query(sql, new MemberRowMapper(), url).stream();
	}
}
