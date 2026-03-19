package org.openldes.server.ingest.kafka.model;

public record KafkaConsumerRequest(String collection, String topic, String mimeType) {
}
