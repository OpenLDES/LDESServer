package org.openldes.server.admin.domain.kafkasource;

import org.openldes.server.domain.model.KafkaSourceProperties;

import java.util.List;

public interface KafkaSourceRepository {
	void save(KafkaSourceProperties kafkaSource, Integer eventStreamId);
	List<KafkaSourceProperties> getAll();

	void deleteWithTopic(String topic);
}
