package org.openldes.server.domain.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class RdfMediaTypeTest {

    @Test
    void getMediaTypes() {
        List<MediaType> mediaTypes = RdfMediaType.getMediaTypes();
        assertThat(mediaTypes).hasSize(8);
        assertThat(mediaTypes).contains(MediaType.valueOf("text/turtle"));
        assertThat(mediaTypes).contains(MediaType.valueOf("application/rdf+xml"));
        assertThat(mediaTypes).contains(MediaType.valueOf("application/n-triples"));
        assertThat(mediaTypes).contains(MediaType.valueOf("application/n-quads"));
        assertThat(mediaTypes).contains(MediaType.valueOf("application/ld+json"));
        assertThat(mediaTypes).contains(MediaType.valueOf("text/html"));
        assertThat(mediaTypes).contains(MediaType.valueOf("text/event-stream"));
        assertThat(mediaTypes).contains(MediaType.valueOf("application/rdf+protobuf"));
    }
}
