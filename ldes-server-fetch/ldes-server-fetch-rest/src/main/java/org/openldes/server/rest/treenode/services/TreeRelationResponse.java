package org.openldes.server.rest.treenode.services;

import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;
import static org.apache.jena.rdf.model.ResourceFactory.createTypedLiteral;
import static org.openldes.server.domain.constants.RdfConstants.RDF_SYNTAX_TYPE;
import static org.openldes.server.domain.constants.RdfConstants.TREE_NODE;
import static org.openldes.server.domain.constants.RdfConstants.TREE_PATH;
import static org.openldes.server.domain.constants.RdfConstants.TREE_RELATION;
import static org.openldes.server.domain.constants.RdfConstants.TREE_VALUE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

public class TreeRelationResponse {
	private final String treePath;
	private final String treeNode;
	private final String treeValue;
	private final String treeValueType;
	private final String relation;

	public TreeRelationResponse(String treePath, String treeNode, String treeValue, String treeValueType,
			String relation) {
		this.treePath = treePath;
		this.treeNode = treeNode;
		this.treeValue = treeValue;
		this.treeValueType = treeValueType;
		this.relation = relation;
	}

	public List<Statement> convertToStatements(final String treeNodeId) {
		List<Statement> statements = new ArrayList<>();
		Resource treeRelationNode = createResource();
		statements.add(createStatement(createResource(treeNodeId), TREE_RELATION, treeRelationNode));
		if (hasMeaningfulValue(treeValue)) {
			if(treeValueType.equals(XSDDatatype.XSDanyURI.getURI())) {
				statements.add(createStatement(treeRelationNode, TREE_VALUE, createResource(treeValue)));
			} else {
				statements.add(createStatement(treeRelationNode, TREE_VALUE, createTypedLiteral(treeValue,
						TypeMapper.getInstance().getTypeByName(treeValueType))));
			}
		}
		addStatementIfMeaningful(statements, treeRelationNode, TREE_PATH, treePath);
		addStatementIfMeaningful(statements, treeRelationNode, TREE_NODE,
				treeNode);
		addStatementIfMeaningful(statements, treeRelationNode, RDF_SYNTAX_TYPE, relation);
		return Collections.unmodifiableList(statements);
	}

	private void addStatementIfMeaningful(List<Statement> statements, Resource subject, Property predicate,
			String objectContent) {
		if (hasMeaningfulValue(objectContent)) {
			statements.add(createStatement(subject, predicate, createResource(objectContent)));
		}
	}

	private boolean hasMeaningfulValue(String objectContent) {
		return objectContent != null && !objectContent.isEmpty();
	}

}
