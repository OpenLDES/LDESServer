package org.openldes.server.fragmentisers.reference.bucketising;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openldes.server.domain.constants.RdfConstants.RDF_SYNTAX_TYPE;
import static org.openldes.server.domain.constants.ServerConstants.DEFAULT_BUCKET_STRING;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openldes.server.fragmentisers.reference.config.ReferenceConfig;

class ReferenceBucketiserTest {

	private ReferenceBucketiser referenceBucketiser;

	private final String memberId = "https://data.vlaanderen.be/id/perceel/13374D0779-00D003/2022-11-29T11:37:27+01:00";

	@BeforeEach
	void setUp() {
		final ReferenceConfig referenceConfig = new ReferenceConfig(RDF_SYNTAX_TYPE.getURI());
		referenceBucketiser = new ReferenceBucketiser(referenceConfig);
	}

	@Test
	void shouldReturnSetOfFoundResources() {
		Model model = RDFParser.source("member-with-two-types.ttl").toModel();

		assertThat(referenceBucketiser.createReferences(memberId, model))
				.hasSize(2)
				.contains("https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel")
				.contains("https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Gebouw");
	}

	@Test
	void shouldReturnDefaultBucketString() {
		Model model = RDFParser.source("member-with-two-types.ttl").toModel();

		assertThat(referenceBucketiser.createReferences("faulty", model))
				.hasSize(1)
				.contains(DEFAULT_BUCKET_STRING);
	}

	@Test
	void when_MemberHasInvalidURI_Then_ReturnOnlyCorrectBucket() {
		Model model = RDFParser.source("member-with-two-types-faulty.ttl").toModel();

		assertThat(referenceBucketiser.createReferences(memberId, model))
				.hasSize(1)
				.contains("https://basisregisters.vlaanderen.be/implementatiemodel/gebouwenregister#Perceel");
	}

	@Test
	void shouldSkipResultsThatAreNotUris() {
		Model model = RDFParser.source("member-with-string-type.ttl").toModel();

		assertThat(referenceBucketiser.createReferences(memberId, model))
				.hasSize(1)
				.contains(DEFAULT_BUCKET_STRING);
	}

}
