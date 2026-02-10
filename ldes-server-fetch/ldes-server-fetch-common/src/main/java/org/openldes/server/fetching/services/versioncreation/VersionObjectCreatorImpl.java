package org.openldes.server.fetching.services.versioncreation;

import org.openldes.server.domain.converter.VersionObjectModelBuilder;
import org.apache.jena.rdf.model.Model;

import java.time.LocalDateTime;

public class VersionObjectCreatorImpl implements VersionObjectCreator {
    private final String versionOfPath;
    private final String timestampPath;

    public VersionObjectCreatorImpl(String versionOfPath, String timestampPath) {
        this.versionOfPath = versionOfPath;
        this.timestampPath = timestampPath;
    }

    @Override
    public Model createFromMember(String subject, Model model, String versionOf, LocalDateTime timestamp) {
        return VersionObjectModelBuilder.create()
                .withMemberSubject(subject)
                .withVersionOfProperties(versionOfPath, versionOf)
                .withTimestampProperties(timestampPath, timestamp)
                .withModel(model)
                .buildVersionObjectModel();
    }


}
