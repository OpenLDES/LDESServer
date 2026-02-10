package org.openldes.server.domain.model;

public record KafkaSourceProperties (String collection, String topic, String mimeType) {
}
