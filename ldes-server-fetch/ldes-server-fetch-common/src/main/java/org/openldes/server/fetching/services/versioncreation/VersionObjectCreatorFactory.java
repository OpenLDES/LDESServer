package org.openldes.server.fetching.services.versioncreation;

import org.openldes.server.domain.model.EventStream;

public class VersionObjectCreatorFactory {
    private VersionObjectCreatorFactory() {
    }

    public static VersionObjectCreator createVersionObjectCreator(EventStream eventStream) {
        if(!eventStream.isVersionCreationEnabled()) {
            return (subject, model, versionOf, timestamp) -> model;
        }
        return new VersionObjectCreatorImpl(eventStream.getVersionOfPath(), eventStream.getTimestampPath());
    }
}
