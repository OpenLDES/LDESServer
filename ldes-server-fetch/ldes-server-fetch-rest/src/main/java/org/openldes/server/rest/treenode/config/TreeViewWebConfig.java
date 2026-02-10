package org.openldes.server.rest.treenode.config;

import org.openldes.server.domain.converter.HttpModelConverter;
import org.openldes.server.domain.converter.PrefixAdder;
import org.openldes.server.domain.converter.RdfModelConverter;
import org.openldes.server.fetching.entities.TreeNode;
import org.openldes.server.rest.treenode.converters.TreeNodeHttpConverter;
import org.openldes.server.rest.treenode.services.TreeNodeConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

@Configuration
public class TreeViewWebConfig {

	@Bean
	public HttpMessageConverter<TreeNode> treeNodeHttpConverter(
			final TreeNodeConverter treeNodeConverter, final RdfModelConverter rdfModelConverter) {
		return new TreeNodeHttpConverter(treeNodeConverter, rdfModelConverter);
	}

	@ConditionalOnMissingBean
	@Bean
	public HttpModelConverter modelConverter(final PrefixAdder prefixAdder, RdfModelConverter rdfModelConverter) {
		return new HttpModelConverter(prefixAdder, rdfModelConverter);
	}
}
