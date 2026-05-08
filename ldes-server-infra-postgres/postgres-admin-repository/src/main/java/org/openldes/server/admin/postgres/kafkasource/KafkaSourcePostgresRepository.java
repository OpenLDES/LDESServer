package org.openldes.server.admin.postgres.kafkasource;

import java.util.List;
import org.openldes.server.admin.domain.kafkasource.KafkaSourceRepository;
import org.openldes.server.admin.postgres.kafkasource.entity.KafkaSourceEntity;
import org.openldes.server.admin.postgres.kafkasource.repository.KafkaSourceEntityRepository;
import org.openldes.server.domain.model.KafkaSourceProperties;
import org.springframework.stereotype.Repository;

@Repository
public class KafkaSourcePostgresRepository implements KafkaSourceRepository {
	private final KafkaSourceEntityRepository repository;

	public KafkaSourcePostgresRepository(KafkaSourceEntityRepository repository) {
		this.repository = repository;
	}

	@Override
	public void save(KafkaSourceProperties kafkaSource, Integer eventStreamId) {
		repository.save(new KafkaSourceEntity(eventStreamId, kafkaSource.collection(), kafkaSource.topic(), kafkaSource.mimeType()));
	}

	@Override
	public List<KafkaSourceProperties> getAll() {
		return repository.findAll().stream()
				.map(kafkaSourceEntity ->
						new KafkaSourceProperties(kafkaSourceEntity.getCollection(),
								kafkaSourceEntity.getTopic(),
								kafkaSourceEntity.getMimeType()))
				.toList();
	}

	@Override
	public void deleteWithTopic(String topic) {
		repository.deleteByTopic(topic);
	}
}
