package org.openldes.server.admin.postgres.kafkasource.repository;

import org.openldes.server.admin.postgres.kafkasource.entity.KafkaSourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KafkaSourceEntityRepository extends JpaRepository<KafkaSourceEntity, Integer> {
	void deleteByTopic(String topic);

}
