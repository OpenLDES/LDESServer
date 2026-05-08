package org.openldes.server.ingest.collection;

import java.util.Optional;
import org.openldes.server.ingest.extractor.MemberExtractor;

public interface MemberExtractorCollection {
   Optional<MemberExtractor> getMemberExtractor(String collection);
   void addMemberExtractor(String collectionName, MemberExtractor memberExtractor);
   void deleteMemberExtractor(String collectionName);
}
