package org.openldes.server.rest.eventstream.config;

import org.openldes.server.admin.spi.EventStreamTO;
import org.openldes.server.admin.spi.EventStreamWriter;
import org.openldes.server.domain.converter.HttpModelConverter;
import org.openldes.server.domain.converter.PrefixAdder;
import org.openldes.server.domain.converter.RdfModelConverter;
import org.openldes.server.rest.eventstream.converters.EventStreamResponseHttpConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

@Configuration
public class EventStreamWebConfig {
	public static final String DEFAULT_RDF_MEDIA_TYPE = "text/turtle";

	@Bean
	public HttpMessageConverter<EventStreamTO> eventStreamResponseHttpMessageConverter(
			EventStreamWriter eventStreamWriter, RdfModelConverter rdfModelConverter) {
		return new EventStreamResponseHttpConverter(eventStreamWriter, rdfModelConverter);
	}

	@ConditionalOnMissingBean
	@Bean
	public HttpModelConverter modelConverter(final PrefixAdder prefixAdder, final RdfModelConverter rdfModelConverter) {
		return new HttpModelConverter(prefixAdder, rdfModelConverter);
	}

}
