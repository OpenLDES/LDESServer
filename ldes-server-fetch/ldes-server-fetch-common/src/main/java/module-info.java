module ldes.fetch.domain {

    exports org.openldes.server.fetching.services;
    exports org.openldes.server.fetching.repository;
    exports org.openldes.server.fetching.entities;
	exports org.openldes.server.fetching.valueobjects;

	requires ldes.domain;

    requires spring.context;
    requires spring.beans;
    requires micrometer.core;
    requires org.jetbrains.annotations;
    requires org.apache.jena.core;
}