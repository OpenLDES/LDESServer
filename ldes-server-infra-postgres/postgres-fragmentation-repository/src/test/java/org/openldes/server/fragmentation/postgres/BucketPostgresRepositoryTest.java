package org.openldes.server.fragmentation.postgres;

import org.openldes.server.domain.model.ViewName;
import org.openldes.server.fragmentation.entities.Bucket;
import org.openldes.server.fragmentation.valueobjects.BucketDescriptor;
import org.openldes.server.fragmentation.valueobjects.BucketDescriptorPair;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class BucketPostgresRepositoryTest extends PostgresBucketisationIntegrationTest {
	private static final ViewName VIEW_NAME = new ViewName("collection", "name");
	@Autowired
	private BucketPostgresRepository bucketPostgresRepository;

	@Test
	@Sql("./init-bucketReader.sql")
	void test_BucketRetrieval() {
		final Bucket expectedBucket = Bucket.createRootBucketForView(VIEW_NAME);

		final Optional<Bucket> retrievedBucket = bucketPostgresRepository.retrieveRootBucket(VIEW_NAME);

		assertThat(retrievedBucket).contains(expectedBucket);
	}

	@Test
	void test_EmptyRetrieval() {
		final Optional<Bucket> retrievedBucket = bucketPostgresRepository.retrieveRootBucket(VIEW_NAME);

		assertThat(retrievedBucket).isEmpty();
	}

	@Test
	@Sql("./init-bucketWriter.sql")
	void test_Insertion() {
		final Bucket bucketToSave = new Bucket(BucketDescriptor.of(new BucketDescriptorPair("key", "value"), new BucketDescriptorPair("k", "v")), VIEW_NAME);
		final Bucket expectedSavedBucket = new Bucket(1L, BucketDescriptor.of(new BucketDescriptorPair("key", "value"), new BucketDescriptorPair("k", "v")), VIEW_NAME, List.of(), 0);

		final Bucket result = bucketPostgresRepository.insertRootBucket(bucketToSave);

		assertThat(result)
				.usingRecursiveComparison()
				.isEqualTo(expectedSavedBucket);
	}
}