package org.openldes.server.fragmentation.postgres;

import java.util.List;
import org.openldes.server.fragmentation.entities.UnprocessedView;
import org.openldes.server.fragmentation.postgres.mapper.UnprocessedViewRowMapper;
import org.openldes.server.fragmentation.repository.UnprocessedViewRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UnprocessedViewPostgresRepository implements UnprocessedViewRepository {
	public static final String SQL = "select * from unprocessed_views";
	private final JdbcTemplate jdbcTemplate;

	public UnprocessedViewPostgresRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UnprocessedView> findAll() {
		return jdbcTemplate.query(SQL, new UnprocessedViewRowMapper());
	}
}
