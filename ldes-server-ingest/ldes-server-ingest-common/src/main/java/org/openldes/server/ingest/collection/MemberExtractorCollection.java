package org.openldes.server.ingest.collection;

import org.openldes.server.ingest.extractor.MemberExtractor;

import java.util.Optional;

public interface MemberExtractorCollection {
   Optional<MemberExtractor> getMemberExtractor(String collection);
   void addMemberExtractor(String collectionName, MemberExtractor memberExtractor);
   void deleteMemberExtractor(String collectionName);
}
