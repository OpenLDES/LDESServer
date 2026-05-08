open module ldes.domain {

    exports org.openldes.server.domain.converter;
    exports org.openldes.server.domain.exceptions;
    exports org.openldes.server.domain.constants;
    exports org.openldes.server.domain.model;
    exports org.openldes.server.domain.rest;
    exports org.openldes.server.domain.encodig;
    exports org.openldes.server.domain.versioning;

    // Events
    exports org.openldes.server.domain.events.retention;
    exports org.openldes.server.domain.events.admin;
	exports org.openldes.server.domain.services;
    exports org.openldes.server.domain.collections;

    requires spring.web;
    requires spring.context;
    requires spring.beans;
    requires spring.boot;
    requires spring.core;
    requires spring.data.commons;
    requires org.apache.jena.core;
    requires org.apache.jena.arq;
    requires org.apache.commons.lang3;
    requires org.apache.jena.shacl;
    requires org.slf4j;
    requires micrometer.core;
    requires org.apache.tomcat.embed.core;
    requires titanium.json.ld;
	requires spring.boot.autoconfigure;

}
