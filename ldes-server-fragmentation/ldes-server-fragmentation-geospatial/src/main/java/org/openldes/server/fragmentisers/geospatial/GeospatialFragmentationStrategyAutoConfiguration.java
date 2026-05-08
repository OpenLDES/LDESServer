package org.openldes.server.fragmentisers.geospatial;

import static org.openldes.server.fragmentisers.geospatial.GeospatialFragmentationStrategy.GEOSPATIAL_FRAGMENTATION;

import jakarta.annotation.PostConstruct;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("org.openldes.server")
public class GeospatialFragmentationStrategyAutoConfiguration {

	@SuppressWarnings("java:S6830")
	@Bean(GEOSPATIAL_FRAGMENTATION)
	public GeospatialFragmentationStrategyWrapper geospatialFragmentationStrategyWrapper() {
		return new GeospatialFragmentationStrategyWrapper();
	}

	/**
	 * Building the geospatial database can take some time. If we do not do this on
	 * initialization, the first
	 * ingestion will take a lot of time because of the database that needs to be
	 * constructed.
	 */
	@PostConstruct
	void triggerEspgDatabaseInitializationOnStartup() {
		String example = "<init-server> <http://www.opengis.net/ont/geosparql#asWKT> \"<http://www.opengis.net/def/crs/EPSG/9.9.1/31370> POINT (0,0)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .";
		RDFParser.create().fromString(example).lang(Lang.NQUADS).build().toModel();
	}

}
