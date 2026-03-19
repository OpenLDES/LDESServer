package org.openldes.server.ingest.extractor;

import org.openldes.server.ingest.entities.IngestedMember;
import org.apache.jena.rdf.model.Model;

import java.util.List;

@FunctionalInterface
public interface MemberExtractor {
    List<IngestedMember> extractMembers(Model ingestedModel);
}
