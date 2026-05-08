package org.openldes.server.fetching.services.versioncreation;

import java.time.LocalDateTime;
import org.apache.jena.rdf.model.Model;
import org.openldes.server.domain.converter.VersionObjectModelBuilder;

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
