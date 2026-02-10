package org.openldes.server.fragmentation.valueobjects;

public record BucketRelation(String fromPartialUrl, String toPartialUrl, TreeRelation relation) {
}