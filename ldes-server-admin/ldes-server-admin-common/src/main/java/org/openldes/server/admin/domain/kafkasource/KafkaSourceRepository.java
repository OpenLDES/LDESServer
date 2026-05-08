package org.openldes.server.admin.domain.kafkasource;

import java.util.List;
import org.openldes.server.domain.model.KafkaSourceProperties;

public interface KafkaSourceRepository {
	void save(KafkaSourceProperties kafkaSource, Integer eventStreamId);
	List<KafkaSourceProperties> getAll();

	void deleteWithTopic(String topic);
}
