package org.openldes.server.admin.spi;

import static org.apache.jena.rdf.model.ModelFactory.createDefaultModel;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.openldes.server.domain.constants.RdfConstants.EVENT_STREAM_TYPE;
import static org.openldes.server.domain.constants.RdfConstants.LDES_CREATE_VERSIONS;
import static org.openldes.server.domain.constants.RdfConstants.LDES_EVENT_SOURCE;
import static org.openldes.server.domain.constants.RdfConstants.LDES_SKOLEMIZATION_DOMAIN;
import static org.openldes.server.domain.constants.RdfConstants.LDES_TIMESTAMP_PATH;
import static org.openldes.server.domain.constants.RdfConstants.LDES_VERSION_DELIMITER;
import static org.openldes.server.domain.constants.RdfConstants.LDES_VERSION_OF;
import static org.openldes.server.domain.constants.RdfConstants.RDF_SYNTAX_TYPE;
import static org.openldes.server.domain.constants.RdfConstants.TREE_SHAPE;
import static org.openldes.server.domain.constants.RdfConstants.TREE_VIEW_DESCRIPTION;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.openldes.server.admin.domain.eventstream.exceptions.MissingStatementException;
import org.openldes.server.domain.model.KafkaSourceProperties;
import org.openldes.server.domain.model.ViewSpecification;
import org.springframework.stereotype.Component;

@Component
public class EventStreamReader {
	private final ViewSpecificationConverter viewSpecificationConverter;
	private final RetentionModelExtractor retentionModelExtractor;
	private final KafkaSourceReader kafkaSourceReader;

	public EventStreamReader(ViewSpecificationConverter viewSpecificationConverter, RetentionModelExtractor retentionModelExtractor, KafkaSourceReader kafkaSourceReader) {
		this.viewSpecificationConverter = viewSpecificationConverter;
		this.retentionModelExtractor = retentionModelExtractor;
		this.kafkaSourceReader = kafkaSourceReader;
	}

	public EventStreamTO read(Model model) {
		final String collection = getIdentifier(model, createResource(EVENT_STREAM_TYPE)).map(Resource::getLocalName)
				.orElseThrow(() -> new MissingStatementException("Not blank node with type " + EVENT_STREAM_TYPE));

		final KafkaSourceProperties kafkaSourceProperties = kafkaSourceReader.readKafkaSourceProperties(collection, model);

		return new EventStreamTO.Builder()
				.withCollection(collection)
				.withTimestampPath(getResource(model, LDES_TIMESTAMP_PATH).orElse(null))
				.withVersionOfPath(getResource(model, LDES_VERSION_OF).orElse(null))
				.withVersionDelimiter(getVersionDelimiter(model))
				.withSkolemizationDomain(getResource(model, LDES_SKOLEMIZATION_DOMAIN).orElse(null))
				.withViews(getViews(model, collection))
				.withShacl(getShaclFromModel(model))
				.withEventSourceRetentionPolicies(getEventSourceRetentionPolicies(model))
				.withKafkaSourceProperties(kafkaSourceProperties)
				.build();
	}

	private Optional<Resource> getIdentifier(Model model, Resource object) {
		Optional<Statement> stmtOptional = model.listStatements(null, RDF_SYNTAX_TYPE, object).nextOptional();
		return stmtOptional.map(Statement::getSubject);
	}

	private Optional<String> getResource(Model model, Property predicate) {
		return model.listObjectsOfProperty(predicate)
				.filterKeep(RDFNode::isResource)
				.nextOptional()
				.map(RDFNode::toString);
	}

	private String getVersionDelimiter(Model model) {
		final boolean versionCreationEnabled = model.listStatements(null, LDES_CREATE_VERSIONS, (Resource) null)
				.nextOptional()
				.map(statement -> statement.getLiteral().getBoolean())
				.orElse(false);

		if(!versionCreationEnabled) {
			return null;
		}
		return model.listObjectsOfProperty(LDES_VERSION_DELIMITER)
				.filterKeep(RDFNode::isLiteral)
				.nextOptional()
				.map(node -> node.asLiteral().getString())
				.orElse("/");
	}

	private List<ViewSpecification> getViews(Model model, String collection) {
		return model.listStatements(null, createProperty(TREE_VIEW_DESCRIPTION), (Resource) null).toList().stream()
				.map(statement -> {
					List<Statement> statements = retrieveAllStatements(statement.getObject().asResource(), model);
					statements.add(statement);
					return statements;
				})
				.map(statements -> createDefaultModel().add(statements))
				.map(viewModel -> viewSpecificationConverter.viewFromModel(viewModel, collection))
				.toList();
	}

	private List<Model> getEventSourceRetentionPolicies(Model model) {
		Optional<Statement> eventSourceStatement = model.listStatements(null, LDES_EVENT_SOURCE,  (RDFNode) null).nextOptional();
		if (eventSourceStatement.isEmpty()) {
			return List.of();
		} else {
			Model eventSourceModel = ModelFactory.createDefaultModel().add(retrieveAllStatements(eventSourceStatement.get().getResource(), model));
			return retentionModelExtractor.extractRetentionStatements(eventSourceModel);
		}
	}

	private Model getShaclFromModel(Model model) {
		final Model shaclModel = ModelFactory.createDefaultModel();
		model.listStatements(null, TREE_SHAPE, (Resource) null)
				.nextOptional()
				.ifPresent(statement -> shaclModel.add(retrieveAllStatements(statement.getResource(), model)));
		return shaclModel;
	}

	private List<Statement> retrieveAllStatements(Resource resource, Model model) {
		StmtIterator iterator = model.listStatements(resource, null, (Resource) null);
		List<Statement> statements = new ArrayList<>();

		while (iterator.hasNext()) {
			Statement statement = iterator.nextStatement();
			statements.add(statement);

			if (statement.getObject().isResource()) {
				statements.addAll(retrieveAllStatements(statement.getResource(), model));
			}
		}

		return statements;
	}
}
