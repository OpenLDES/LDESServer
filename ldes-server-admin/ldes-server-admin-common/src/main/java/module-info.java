open module ldes.admin {

	exports org.openldes.server.admin.spi;
	exports org.openldes.server.admin.domain.dcat.dcatdataset.entities;
	exports org.openldes.server.admin.domain.dcat.dcatdataset.repository;
	exports org.openldes.server.admin.domain.dcat.dcatdataset.services;
	exports org.openldes.server.admin.domain.dcat.dcatserver.entities;
	exports org.openldes.server.admin.domain.dcat.dcatserver.repository;
	exports org.openldes.server.admin.domain.dcat.dcatserver.services;
	exports org.openldes.server.admin.domain.eventsource.repository;
	exports org.openldes.server.admin.domain.eventsource.services;
	exports org.openldes.server.admin.domain.eventstream.exceptions;
	exports org.openldes.server.admin.domain.eventstream.repository;
	exports org.openldes.server.admin.domain.eventstream.services;
	exports org.openldes.server.admin.domain.kafkasource;
	exports org.openldes.server.admin.domain.shacl.entities;
	exports org.openldes.server.admin.domain.shacl.repository;
	exports org.openldes.server.admin.domain.shacl.services;
	exports org.openldes.server.admin.domain.validation;
	exports org.openldes.server.admin.domain.validation.dcat;
	exports org.openldes.server.admin.domain.view.service;
	exports org.openldes.server.admin.domain.view.exception;
	exports org.openldes.server.admin.domain.view.repository;

	// LDES dependencies
	requires ldes.domain;

	// external dependencies
	requires spring.boot;
	requires spring.beans;
	requires spring.context;
	requires spring.boot.autoconfigure;
	requires spring.boot.actuator;
	requires spring.boot.actuator.autoconfigure;
	requires org.apache.commons.lang3;
	requires org.apache.tomcat.embed.core;
	requires org.apache.jena.arq;
	requires org.apache.jena.core;
	requires org.apache.jena.shacl;
	requires com.google.common;
	requires org.slf4j;
	requires org.jetbrains.annotations;
	requires micrometer.observation;
	requires micrometer.core;

}