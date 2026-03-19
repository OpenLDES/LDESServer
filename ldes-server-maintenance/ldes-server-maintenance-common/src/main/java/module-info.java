open module ldes.server.maintenance.common {
	requires spring.context;
	requires spring.batch.core;
	requires ldes.domain;
	requires spring.beans;
	exports org.openldes.server.maintenance.services;
	exports org.openldes.server.maintenance.valueobjects;
	exports org.openldes.server.maintenance.repository;
}