package org.openldes.server.fragmentisers.geospatial;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.openldes.server.fragmentisers.geospatial.GeospatialFragmentationStrategyAutoConfiguration.LAMBERT_72_ORIGIN;
import static org.openldes.server.fragmentisers.geospatial.constants.GeospatialConstants.WKT_DATA_TYPE;

import org.apache.jena.datatypes.BaseDatatype;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.apache.jena.geosparql.implementation.datatype.GMLDatatype;
import org.apache.jena.geosparql.implementation.datatype.WKTDatatype;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sys.JenaSystem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GeospatialFragmentationStrategyAutoConfigurationTest {

	private static final String WGS_84_POINT = "POINT (4.35 51.02)";

	private GeospatialFragmentationStrategyAutoConfiguration autoConfiguration;

	@BeforeEach
	void setUp() {
		JenaSystem.init();
		autoConfiguration = new GeospatialFragmentationStrategyAutoConfiguration();
	}

	@AfterEach
	void restoreGeometryDatatypes() {
		TypeMapper.getInstance().registerDatatype(WKTDatatype.INSTANCE);
		TypeMapper.getInstance().registerDatatype(GMLDatatype.INSTANCE);
	}

	@Test
	void triggerEspgDatabaseInitializationOnStartupShouldNotFail() {
		assertDoesNotThrow(() -> autoConfiguration.registerGeometryDatatypesAndInitializeEspgDatabase());
	}

	@Test
	@DisplayName("A geometry resolves to a GeometryWrapper even when Jena never registered the GeoSPARQL datatypes")
	void when_TheGeoSparqlDatatypesAreUnregistered_Then_StartupRegistersThem() {
		// Jena registers geo:wktLiteral from its InitGeoSPARQL subsystem, discovered through
		// META-INF/services. Packaging that drops that service file - jar-with-dependencies used
		// to - leaves the datatype bound to a plain BaseDatatype, which is reproduced here.
		TypeMapper.getInstance().registerDatatype(new BaseDatatype(WKT_DATA_TYPE));
		assertInstanceOf(BaseDatatype.TypedValue.class, parseGeometry(WGS_84_POINT));

		autoConfiguration.registerGeometryDatatypesAndInitializeEspgDatabase();

		assertInstanceOf(WKTDatatype.class, TypeMapper.getInstance().getSafeTypeByName(WKT_DATA_TYPE));
		assertInstanceOf(GeometryWrapper.class, parseGeometry(WGS_84_POINT));
	}

	@Test
	@DisplayName("The geometry that warms up the EPSG database is legal WKT")
	void when_TheWarmUpGeometryIsResolved_Then_ItYieldsAGeometry() {
		assertInstanceOf(GeometryWrapper.class, parseGeometry(LAMBERT_72_ORIGIN));
	}

	/** Resolves a geo:wktLiteral the way an ingested member does: through the type mapper. */
	private Object parseGeometry(String lexicalForm) {
		return ResourceFactory
				.createTypedLiteral(lexicalForm, TypeMapper.getInstance().getSafeTypeByName(WKT_DATA_TYPE))
				.getValue();
	}

}
