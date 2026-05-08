package org.openldes.server.admin.postgres.eventstream.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Optional;
import org.apache.jena.rdf.model.Model;
import org.openldes.server.admin.postgres.dcatdataset.entity.DcatDatasetEntity;
import org.openldes.server.admin.postgres.eventsource.entity.EventSourceEntity;
import org.openldes.server.admin.postgres.shaclshape.entity.ShaclShapeEntity;
import org.openldes.server.admin.postgres.view.entity.ViewEntity;

@Entity
@Table(name = "collections")
public class EventStreamEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collection_id", unique = true, nullable = false, columnDefinition = "SMALLINT")
    private Integer id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(name = "timestamp_path", nullable = false)
    private String timestampPath;

    @Column(name = "version_of_path", nullable = false)
    private String versionOfPath;

    @Column(name = "version_delimiter")
    private String versionDelimiter;

    @Column(name = "is_closed", nullable = false)
    private Boolean closed;

    @Column(name = "skolemization_domain")
    private String skolemizationDomain;

    @OneToMany(mappedBy = "eventStream", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<ViewEntity> views;

    @OneToOne(mappedBy = "eventStream")
    private DcatDatasetEntity datasetEntity;

    @OneToOne(mappedBy = "eventStream", cascade = CascadeType.PERSIST)
    private ShaclShapeEntity shaclShapeEntity;

    @OneToOne(mappedBy = "eventStream", cascade = CascadeType.PERSIST)
    private EventSourceEntity eventSourceEntity;

    public EventStreamEntity() {
    }

    public EventStreamEntity(String name, String timestampPath, String versionOfPath, String versionDelimiter, Boolean closed, String skolemizationDomain) {
        this.name = name;
        this.timestampPath = timestampPath;
        this.versionOfPath = versionOfPath;
        this.versionDelimiter = versionDelimiter;
        this.closed = closed;
        this.skolemizationDomain = skolemizationDomain;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTimestampPath() {
        return timestampPath;
    }

    public String getVersionOfPath() {
        return versionOfPath;
    }

    public String getVersionDelimiter() {
        return versionDelimiter;
    }

    public boolean isClosed() {
        return closed;
    }

    public String getSkolemizationDomain() {
        return skolemizationDomain;
    }

    public List<ViewEntity> getViews() {
        return views;
    }

    public Optional<Model> getDcat() {
        return Optional.ofNullable(datasetEntity).map(DcatDatasetEntity::getModel);
    }

    public ShaclShapeEntity getShaclShapeEntity() {
        return shaclShapeEntity;
    }

    public EventSourceEntity getEventSourceEntity() {
        return eventSourceEntity;
    }

    public void setViews(List<ViewEntity> views) {
        this.views = views;
    }

    public void setShaclShapeEntity(ShaclShapeEntity shaclShapeEntity) {
        this.shaclShapeEntity = shaclShapeEntity;
    }

    public void setEventSourceEntity(EventSourceEntity eventSourceEntity) {
        this.eventSourceEntity = eventSourceEntity;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
