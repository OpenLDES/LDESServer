package org.openldes.server.ingest.skolemization;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;

public class SkolemizedModel {
	private final String skolemUriTemplate;
	private final Model model;
	private final Map<RDFNode, Resource> bnodes = new HashMap<>();

	public SkolemizedModel(String skolemUriTemplate, Model model) {
		this.skolemUriTemplate = skolemUriTemplate;
		this.model = model;
	}

	public Model getModel() {
		if(!hasBNodes(model)) {
			return model;
		}
		return ModelFactory.createDefaultModel()
				.add(model.listStatements().mapWith(this::getModel).toList());
	}

	private boolean hasBNodes(Model model) {
		return model.listStatements().filterKeep(statement -> statement.getSubject().isAnon() || statement.getObject().isAnon()).hasNext();
	}

	private Statement getModel(Statement statement) {
		return StatementBuilder.withPredicate(statement.getPredicate())
				.withSubject(statement.getSubject().isAnon() ? getSkolemizedNode(statement.getSubject()) : statement.getSubject())
				.withObject(statement.getObject().isAnon() ? getSkolemizedNode(statement.getObject()) : statement.getObject())
				.build();
	}

	private Resource getSkolemizedNode(RDFNode rdfNode) {
		return bnodes.computeIfAbsent(rdfNode, bnode -> ResourceFactory.createProperty(skolemUriTemplate.formatted(UUID.randomUUID())));
	}
}
