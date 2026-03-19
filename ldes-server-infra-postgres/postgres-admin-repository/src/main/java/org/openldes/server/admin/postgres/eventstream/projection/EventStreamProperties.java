package org.openldes.server.admin.postgres.eventstream.projection;

public interface EventStreamProperties {
    String getName();
    String getTimestampPath();
    String getVersionOfPath();
    String getVersionDelimiter();
    boolean isClosed();
    String getSkolemizationDomain();
}
