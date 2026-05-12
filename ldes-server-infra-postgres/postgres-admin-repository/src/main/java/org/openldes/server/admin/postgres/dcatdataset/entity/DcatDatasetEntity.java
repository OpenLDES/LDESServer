package org.openldes.server.admin.postgres.dcatdataset.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.apache.jena.rdf.model.Model;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.openldes.server.admin.postgres.ModelConverter;
import org.openldes.server.admin.postgres.eventstream.entity.EventStreamEntity;

@Entity
@Table(name = "dcat_datasets")
public class DcatDatasetEntity {
    @Id
    @Column(name = "collection_id", unique = true, nullable = false)
    private Integer collectionId;

    @MapsId
    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "collection_id", nullable = false)
    private EventStreamEntity eventStream;

    @Column(nullable = false, columnDefinition = "text")
    @Convert(converter = ModelConverter.class)
    private Model model;

    protected DcatDatasetEntity() {}

    public DcatDatasetEntity(EventStreamEntity eventStream) {
        this.eventStream = eventStream;
    }

    public DcatDatasetEntity(EventStreamEntity eventStream, Model model) {
        this.eventStream = eventStream;
        this.model = model;
    }

    public String getCollectionName() {
        return eventStream.getName();
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }
}
