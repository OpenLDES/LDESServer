module ldes.ingest.domain {
    requires ldes.domain;
    requires spring.context;
    requires org.apache.jena.core;
    requires org.apache.jena.shacl;
    requires org.slf4j;
    requires spring.boot.autoconfigure;
    requires org.apache.jena.arq;
    requires micrometer.core;
    requires micrometer.observation;
    exports org.openldes.server.ingest.entities;
    exports org.openldes.server.ingest.repositories;
    exports org.openldes.server.ingest;
}