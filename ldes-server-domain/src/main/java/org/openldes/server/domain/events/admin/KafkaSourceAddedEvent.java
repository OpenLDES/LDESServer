package org.openldes.server.domain.events.admin;

import org.openldes.server.domain.model.KafkaSourceProperties;

public record KafkaSourceAddedEvent(KafkaSourceProperties kafkaSource) {
}
