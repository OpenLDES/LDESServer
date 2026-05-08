package org.openldes.server.retention.repositories.retentionpolicies;

import java.util.HashSet;
import java.util.Set;
import org.openldes.server.domain.events.admin.EventStreamDeletedEvent;
import org.openldes.server.domain.events.admin.ViewAddedEvent;
import org.openldes.server.domain.events.admin.ViewDeletedEvent;
import org.openldes.server.domain.events.admin.ViewInitializationEvent;
import org.openldes.server.domain.events.admin.ViewSupplier;
import org.openldes.server.domain.model.ViewSpecification;
import org.openldes.server.retention.entities.ViewRetentionPolicyProvider;
import org.openldes.server.retention.services.retentionpolicy.creation.RetentionPolicyFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ViewRetentionPolicyCollection implements RetentionPolicyCollection<ViewRetentionPolicyProvider> {

    private final Set<ViewRetentionPolicyProvider> retentionPolicies;
    private final RetentionPolicyFactory retentionPolicyFactory;

    public ViewRetentionPolicyCollection(RetentionPolicyFactory retentionPolicyFactory) {
        this.retentionPolicyFactory = retentionPolicyFactory;
        this.retentionPolicies = new HashSet<>();
    }

    @EventListener(classes = {ViewInitializationEvent.class, ViewAddedEvent.class})
    public void handleViewAddedEvent(ViewSupplier event) {
        addToCollection(event.viewSpecification());
    }

    @EventListener
    public void handleViewDeletedEvent(ViewDeletedEvent event) {
        retentionPolicies.removeIf(retentionPolicy -> retentionPolicy.viewName().equals(event.getViewName()));
    }

    @EventListener
    public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
        retentionPolicies.removeIf(retentionPolicy -> retentionPolicy.viewName().getCollectionName().equals(event.collectionName()));
    }

    @Override
    public Set<ViewRetentionPolicyProvider> getRetentionPolicies() {
        return Set.copyOf(retentionPolicies);
    }

    @Override
    public boolean isEmpty() {
        return retentionPolicies.isEmpty();
    }

    private void addToCollection(ViewSpecification viewSpecification) {
        retentionPolicyFactory
                .extractRetentionPolicy(viewSpecification)
                .map(retentionPolicy -> new ViewRetentionPolicyProvider(viewSpecification.getName(), retentionPolicy))
                .ifPresent(retentionPolicies::add);
    }

}
