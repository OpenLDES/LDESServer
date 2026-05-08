package org.openldes.server.rest.treenode.services;

import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openldes.server.domain.constants.RdfConstants.LDES_EVENT_STREAM_URI;
import static org.openldes.server.domain.constants.RdfConstants.NODE_SHAPE_TYPE;
import static org.openldes.server.domain.constants.RdfConstants.RDF_SYNTAX_TYPE;
import static org.openldes.server.domain.constants.RdfConstants.TREE_SHAPE;

import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.Test;

class EventStreamInfoResponseTest {

    @Test
    void convertToStatements_IncludesMandatoryStatements() {
        String eventStreamId = "http://example.com/eventStream";
        EventStreamInfoResponse response =
                new EventStreamInfoResponse(eventStreamId, "", "", null, List.of());

        List<Statement> statements = response.convertToStatements();

        assertThat(statements).anyMatch(s ->
                s.getSubject().toString().equals(eventStreamId) &&
                        s.getPredicate().equals(RDF_SYNTAX_TYPE) &&
                        s.getObject().toString().equals(LDES_EVENT_STREAM_URI));
    }

    @Test
    void convertToStatements_AddsShapeIfPresent() {
        Model shape = RDFParser.create().fromString("[ a <http://www.w3.org/ns/shacl#NodeShape> ]").lang(Lang.TURTLE).toModel();

        EventStreamInfoResponse response =
                new EventStreamInfoResponse("http://example.com/eventStream", "", "", shape, List.of());

        List<Statement> statements = response.convertToStatements();

        Model model = ModelFactory.createDefaultModel().add(statements);
        Resource shapeSubject = model.listSubjectsWithProperty(RDF.type, createResource(NODE_SHAPE_TYPE)).nextResource();
        NodeIterator nodeIterator = model.listObjectsOfProperty(TREE_SHAPE);
        assertThat(nodeIterator).hasNext();
        assertThat(nodeIterator.next().asResource()).isEqualTo(shapeSubject);
    }

}
