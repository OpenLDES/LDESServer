package org.openldes.server.admin.rest.config;

import org.openldes.server.domain.converter.HttpModelConverter;
import org.openldes.server.domain.converter.PrefixAdder;
import org.openldes.server.domain.converter.RdfModelConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminWebConfig {

	@ConditionalOnMissingBean
	@Bean
	public HttpModelConverter modelConverter(final PrefixAdder prefixAdder, RdfModelConverter rdfModelConverter) {
		return new HttpModelConverter(prefixAdder, rdfModelConverter);
	}
}
