package org.openldes.server.admin.postgres.eventsource.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.openldes.server.admin.postgres.ModelListConverter;
import org.openldes.server.admin.postgres.eventstream.entity.EventStreamEntity;

@Entity
@Table(name = "eventsources")
public class EventSourceEntity {
    @Id
    @Column(name = "collection_id", nullable = false)
    private Integer collectionId;

    @MapsId
    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "collection_id", nullable = false)
    private EventStreamEntity eventStream;

    @Convert(converter = ModelListConverter.class)
    @Column(name = "retention_policies", columnDefinition = "text", nullable = false)
    private List<Model> retentionPolicies;

    public EventSourceEntity() {
    }

    public EventSourceEntity(EventStreamEntity eventStream) {
        this.eventStream = eventStream;
    }

    public EventSourceEntity(EventStreamEntity eventStream, List<Model> retentionPolicies) {
        this.eventStream = eventStream;
        this.retentionPolicies = retentionPolicies;
    }

    public String getCollectionName() {
        return eventStream.getName();
    }

    public List<Model> getRetentionPolicies() {
        return retentionPolicies;
    }

    public void setRetentionPolicies(List<Model> retentionPolicies) {
        this.retentionPolicies = retentionPolicies;
    }
}
