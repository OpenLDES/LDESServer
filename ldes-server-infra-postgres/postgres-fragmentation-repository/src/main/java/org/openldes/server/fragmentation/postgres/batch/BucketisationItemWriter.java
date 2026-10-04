package org.openldes.server.fragmentation.postgres.batch;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.openldes.server.fragmentation.entities.Bucket;
import org.openldes.server.fragmentation.entities.BucketisedMember;
import org.openldes.server.fragmentation.postgres.batch.chunk.ChunkCollector;
import org.openldes.server.fragmentation.valueobjects.BucketRelation;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class BucketisationItemWriter implements ItemWriter<Bucket> {
	private final ItemWriter<Bucket> bucketItemWriter;
	private final ItemWriter<Bucket> pageItemWriter;
	private final ItemWriter<BucketisedMember> bucketisedMemberItemWriter;
	private final ItemWriter<BucketRelation> bucketRelationWriter;
	private final JdbcTemplate jdbcTemplate;
	private final long viewId;

	public BucketisationItemWriter(ItemWriter<Bucket> bucketItemWriter,
	                               ItemWriter<Bucket> pageItemWriter,
	                               ItemWriter<BucketisedMember> bucketisedMemberItemWriter,
	                               ItemWriter<BucketRelation> bucketRelationWriter,
	                               JdbcTemplate jdbcTemplate,
	                               @Value("#{jobParameters['viewId']}") long viewId) {
		this.bucketItemWriter = bucketItemWriter;
		this.pageItemWriter = pageItemWriter;
		this.bucketisedMemberItemWriter = bucketisedMemberItemWriter;
		this.bucketRelationWriter = bucketRelationWriter;
		this.jdbcTemplate = jdbcTemplate;
		this.viewId = viewId;
	}

	@Override
	public void write(Chunk<? extends Bucket> rootBucketChunk) throws Exception {
		for (var rootbucket : rootBucketChunk) {
			final Chunk<Bucket> flatBucketChunk = new Chunk<>(rootbucket.getBucketTree());
			bucketItemWriter.write(flatBucketChunk);
			pageItemWriter.write(flatBucketChunk);
			bucketRelationWriter.write(extractAllBucketRelations(rootbucket));
			var members = extractAllMembers(flatBucketChunk);
			bucketisedMemberItemWriter.write(members);

			var uniqueMemberIds = members.getItems().stream()
					.map(BucketisedMember::memberId)
					.distinct()
					.toList();
			markMembersAsFragmented(uniqueMemberIds);
			updateViewStats(members.getItems().getLast().memberId(), uniqueMemberIds.size());
		}
	}

	/**
	 * Members are fragmented once per view, so only the view that is currently being bucketised may stop
	 * considering them. See <a href="https://github.com/OpenLDES/LDESServer/issues/48">issue 48</a>.
	 */
	private void markMembersAsFragmented(List<Long> memberIds) {
		String sql = """
				update processable_members set
				      is_fragmented = true
				    where view_id = ? and member_id = ?;
				""";

		jdbcTemplate.batchUpdate(sql, memberIds.stream().map(memberId -> new Object[]{viewId, memberId}).toList());
	}

	private void updateViewStats(long lastMemberId, long uniqueMemberCount) {
		String sql = """
				update view_stats vs set
				      bucketized_count = vs.bucketized_count + ?,
				      bucketized_last_id = ?
				    where view_id = ?;
				""";

		jdbcTemplate.update(sql, uniqueMemberCount, lastMemberId, viewId);
	}


	private static Chunk<BucketisedMember> extractAllMembers(Chunk<? extends Bucket> flatBucketChunk) {
		return flatBucketChunk.getItems().stream()
				.map(Bucket::getMember)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.sorted(Comparator.comparing(BucketisedMember::memberId))
				.collect(new ChunkCollector<>());
	}

	private static Chunk<BucketRelation> extractAllBucketRelations(Bucket rootbucket) {
		return rootbucket.getBucketTree().stream()
				.flatMap(bucket -> bucket.getChildRelations().stream())
				.distinct()
				.collect(new ChunkCollector<>());
	}

}
