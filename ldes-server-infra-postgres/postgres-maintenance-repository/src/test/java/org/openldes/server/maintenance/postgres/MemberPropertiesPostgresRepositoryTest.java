package org.openldes.server.maintenance.postgres;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openldes.server.domain.model.ViewName;
import org.openldes.server.maintenance.postgres.mapper.MemberPropertiesEntityMapper;
import org.openldes.server.maintenance.postgres.projection.RetentionMemberProjection;
import org.openldes.server.maintenance.postgres.repository.RetentionMemberEntityRepository;
import org.openldes.server.retention.entities.MemberProperties;
import org.openldes.server.retention.services.retentionpolicy.definition.timeandversionbased.TimeAndVersionBasedRetentionPolicy;
import org.openldes.server.retention.services.retentionpolicy.definition.versionbased.VersionBasedRetentionPolicy;

/**
 * Version-based retention must keep the most recent versions according to the timestamp
 * ({@code ldes:timestampPath}), not the versions that happened to be ingested last.
 * The members below are ingested (= get their id) in a non-chronological order.
 */
@ExtendWith(MockitoExtension.class)
class MemberPropertiesPostgresRepositoryTest {
	private static final String COLLECTION = "collection";
	private static final ViewName VIEW = new ViewName(COLLECTION, "view");

	// state object A: newest version ingested first
	private static final RetentionMemberProjection A_V2 = member(1, "A", "2026-09-04T00:00:00");
	private static final RetentionMemberProjection A_V1 = member(2, "A", "2026-01-09T00:00:00");
	// state object B: chronological ingest (control)
	private static final RetentionMemberProjection B_V1 = member(3, "B", "2026-01-09T00:00:00");
	private static final RetentionMemberProjection B_V2 = member(4, "B", "2026-09-04T00:00:00");
	// state object C: oldest version ingested last
	private static final RetentionMemberProjection C_V2 = member(5, "C", "2026-05-01T00:00:00");
	private static final RetentionMemberProjection C_V3 = member(6, "C", "2026-09-04T00:00:00");
	private static final RetentionMemberProjection C_V1 = member(7, "C", "2025-11-28T00:00:00");

	private static final List<RetentionMemberProjection> MEMBERS = List.of(A_V2, A_V1, B_V1, B_V2, C_V2, C_V3, C_V1);

	@Mock
	private EntityManager entityManager;
	@Mock
	private RetentionMemberEntityRepository memberEntityRepository;

	private MemberPropertiesPostgresRepository repository;

	@BeforeEach
	void setUp() {
		repository = new MemberPropertiesPostgresRepository(entityManager, new MemberPropertiesEntityMapper(), memberEntityRepository);
	}

	@Test
	@DisplayName("version-based view retention keeps the latest version by timestamp, not the last ingested one")
	void versionBasedView_keepsLatestByTimestamp() {
		when(memberEntityRepository.findAllByViewName("view", COLLECTION)).thenReturn(MEMBERS);

		List<Long> expired = repository.findExpiredMembers(VIEW, new VersionBasedRetentionPolicy(1));

		assertThat(expired).containsExactlyInAnyOrder(A_V1.getId(), B_V1.getId(), C_V2.getId(), C_V1.getId());
	}

	@Test
	@DisplayName("version-based view retention with amount 2 keeps the two latest versions by timestamp")
	void versionBasedView_amountTwo() {
		when(memberEntityRepository.findAllByViewName("view", COLLECTION)).thenReturn(MEMBERS);

		List<Long> expired = repository.findExpiredMembers(VIEW, new VersionBasedRetentionPolicy(2));

		assertThat(expired).containsExactly(C_V1.getId());
	}

	@Test
	@DisplayName("time-and-version-based view retention keeps the latest version by timestamp")
	void timeAndVersionBasedView_keepsLatestByTimestamp() {
		when(memberEntityRepository.findAllByViewNameAndTimestampBefore(eq("view"), eq(COLLECTION), any(LocalDateTime.class)))
				.thenReturn(MEMBERS);

		List<Long> expired = repository.findExpiredMembers(VIEW, new TimeAndVersionBasedRetentionPolicy(Duration.ofDays(1), 1));

		assertThat(expired).containsExactlyInAnyOrder(A_V1.getId(), B_V1.getId(), C_V2.getId(), C_V1.getId());
	}

	@Test
	@DisplayName("version-based event source retention keeps the latest version by timestamp")
	void versionBasedEventSource_keepsLatestByTimestamp() {
		when(memberEntityRepository.findAllByCollectionName(COLLECTION)).thenReturn(MEMBERS);

		List<Long> expired = repository.retrieveExpiredMembers(COLLECTION, new VersionBasedRetentionPolicy(1))
				.map(MemberProperties::id)
				.toList();

		assertThat(expired).containsExactlyInAnyOrder(A_V1.getId(), B_V1.getId(), C_V2.getId(), C_V1.getId());
	}

	@Test
	@DisplayName("time-and-version-based event source retention keeps the latest version by timestamp")
	void timeAndVersionBasedEventSource_keepsLatestByTimestamp() {
		when(memberEntityRepository.findAllByCollectionNameAndTimestampBefore(eq(COLLECTION), any(LocalDateTime.class)))
				.thenReturn(MEMBERS);

		List<Long> expired = repository.retrieveExpiredMembers(COLLECTION, new TimeAndVersionBasedRetentionPolicy(Duration.ofDays(1), 1))
				.map(MemberProperties::id)
				.toList();

		assertThat(expired).containsExactlyInAnyOrder(A_V1.getId(), B_V1.getId(), C_V2.getId(), C_V1.getId());
	}

	@Test
	@DisplayName("with equal timestamps the member ingested last is kept")
	void equalTimestamps_idIsTieBreaker() {
		RetentionMemberProjection first = member(10, "D", "2026-09-04T00:00:00");
		RetentionMemberProjection second = member(11, "D", "2026-09-04T00:00:00");
		when(memberEntityRepository.findAllByViewName("view", COLLECTION)).thenReturn(List.of(second, first));

		List<Long> expired = repository.findExpiredMembers(VIEW, new VersionBasedRetentionPolicy(1));

		assertThat(expired).containsExactly(first.getId());
	}

	private static RetentionMemberProjection member(long id, String versionOf, String timestamp) {
		return new TestMember(id, versionOf, LocalDateTime.parse(timestamp));
	}

	private record TestMember(long id, String versionOf, LocalDateTime timestamp) implements RetentionMemberProjection {
		@Override
		public long getId() {
			return id;
		}

		@Override
		public String getVersionOf() {
			return versionOf;
		}

		@Override
		public LocalDateTime getTimestamp() {
			return timestamp;
		}

		@Override
		public Boolean getInView() {
			return true;
		}

		@Override
		public Boolean getInEventSource() {
			return true;
		}

		@Override
		public String getCollectionName() {
			return COLLECTION;
		}
	}
}
