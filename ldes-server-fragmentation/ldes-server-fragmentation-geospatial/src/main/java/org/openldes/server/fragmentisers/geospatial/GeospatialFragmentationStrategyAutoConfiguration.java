package org.openldes.server.fragmentisers.geospatial;

import static org.openldes.server.fragmentisers.geospatial.GeospatialFragmentationStrategy.GEOSPATIAL_FRAGMENTATION;
import static org.openldes.server.fragmentisers.geospatial.constants.GeospatialConstants.WKT_DATA_TYPE;

import jakarta.annotation.PostConstruct;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.apache.jena.geosparql.implementation.datatype.GMLDatatype;
import org.apache.jena.geosparql.implementation.datatype.WKTDatatype;
import org.apache.jena.geosparql.implementation.vocabulary.SRS_URI;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sys.JenaSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("org.openldes.server")
public class GeospatialFragmentationStrategyAutoConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(GeospatialFragmentationStrategyAutoConfiguration.class);

	/** The Lambert 72 origin: a projected CRS, so resolving it forces the EPSG database to be built. */
	static final String LAMBERT_72_ORIGIN = "<http://www.opengis.net/def/crs/EPSG/9.9.1/31370> POINT (0 0)";

	@SuppressWarnings("java:S6830")
	@Bean(GEOSPATIAL_FRAGMENTATION)
	public GeospatialFragmentationStrategyWrapper geospatialFragmentationStrategyWrapper() {
		return new GeospatialFragmentationStrategyWrapper();
	}

	@PostConstruct
	void registerGeometryDatatypesAndInitializeEspgDatabase() {
		registerGeometryDatatypes();
		initializeEspgDatabase();
	}

	/**
	 * Jena maps geo:wktLiteral onto a GeometryWrapper only once the GeoSPARQL datatypes are in the
	 * type mapper. Jena registers them from its InitGeoSPARQL subsystem, which is discovered
	 * through META-INF/services, so any packaging that drops that service file leaves the datatype
	 * unregistered. A member's geometry then parses into a plain BaseDatatype.TypedValue,
	 * GeospatialBucketiser cannot cast it, and every member silently ends up in the default bucket.
	 * <p>
	 * Registering here does not depend on how this module is packaged. The check afterwards turns
	 * a would-be silent mis-fragmentation of the whole event stream into a startup failure.
	 */
	private void registerGeometryDatatypes() {
		JenaSystem.init();

		// Registered straight on the type mapper rather than through
		// GeometryDatatype.registerDatatypes(), which runs off a static holder and is therefore a
		// no-op as soon as anything else has touched that class.
		TypeMapper typeMapper = TypeMapper.getInstance();
		typeMapper.registerDatatype(WKTDatatype.INSTANCE);
		typeMapper.registerDatatype(GMLDatatype.INSTANCE);

		RDFDatatype registered = typeMapper.getSafeTypeByName(WKT_DATA_TYPE);
		if (!(registered instanceof WKTDatatype)) {
			throw new IllegalStateException(
					"%s is bound to %s instead of the GeoSPARQL WKTDatatype, so no member can be fragmented geospatially."
							.formatted(WKT_DATA_TYPE, registered.getClass().getName()));
		}
	}

	/**
	 * Building the EPSG database can take some time. If we do not do this on initialization, the
	 * first ingestion will take a lot of time because of the database that needs to be constructed.
	 * <p>
	 * The geometry is resolved through the type mapper and reprojected, because that is what an
	 * ingested member goes through: merely parsing the literal leaves its value untouched and never
	 * reaches Apache SIS.
	 */
	private void initializeEspgDatabase() {
		try {
			GeometryWrapper origin = (GeometryWrapper) ResourceFactory
					.createTypedLiteral(LAMBERT_72_ORIGIN, TypeMapper.getInstance().getSafeTypeByName(WKT_DATA_TYPE))
					.getValue();
			origin.convertSRS(SRS_URI.WGS84_CRS);
		} catch (Exception exception) {
			// Depends on the environment (SIS_DATA and a writable data directory), so it must not
			// keep the server from starting: the first geospatial ingest simply pays the cost.
			LOGGER.warn("Could not initialize the EPSG database on startup. Reason: {}", exception.getMessage());
		}
	}

}
