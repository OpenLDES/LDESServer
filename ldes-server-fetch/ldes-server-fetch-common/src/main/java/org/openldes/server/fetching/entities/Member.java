package org.openldes.server.fetching.entities;

import org.apache.jena.rdf.model.Model;

public record Member(String subject, Model model) {
}
