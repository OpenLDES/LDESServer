package org.openldes.server.ingest.extractor;

import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.openldes.server.ingest.entities.IngestedMember;

@FunctionalInterface
public interface MemberExtractor {
    List<IngestedMember> extractMembers(Model ingestedModel);
}
