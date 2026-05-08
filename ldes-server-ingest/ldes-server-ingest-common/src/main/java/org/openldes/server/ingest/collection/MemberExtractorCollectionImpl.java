package org.openldes.server.ingest.collection;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.openldes.server.domain.events.admin.EventStreamCreatedEvent;
import org.openldes.server.domain.events.admin.EventStreamDeletedEvent;
import org.openldes.server.domain.model.EventStream;
import org.openldes.server.ingest.extractor.MemberExtractor;
import org.openldes.server.ingest.extractor.MemberExtractorFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MemberExtractorCollectionImpl implements MemberExtractorCollection {
    private final Map<String, MemberExtractor> memberExtractors = new HashMap<>();

    @Override
    public Optional<MemberExtractor> getMemberExtractor(String collectionName) {
        return Optional.ofNullable(memberExtractors.get(collectionName));
    }

    @Override
    public void addMemberExtractor(String collectionName, MemberExtractor memberExtractor) {
        memberExtractors.put(collectionName, memberExtractor);
    }

    @Override
    public void deleteMemberExtractor(String collectionName) {
        memberExtractors.remove(collectionName);
    }

    @EventListener
    public void handleEventStreamCreatedEvent(EventStreamCreatedEvent event) {
        final EventStream eventStream = event.eventStream();
        addMemberExtractor(eventStream.getCollection(), MemberExtractorFactory.createMemberExtractor(eventStream));
    }

    @EventListener
    public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
        deleteMemberExtractor(event.collectionName());
    }
}
