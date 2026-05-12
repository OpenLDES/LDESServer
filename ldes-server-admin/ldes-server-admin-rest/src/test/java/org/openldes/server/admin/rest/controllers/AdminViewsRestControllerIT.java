package org.openldes.server.admin.rest.controllers;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.openldes.server.admin.rest.config.SpringIntegrationTest;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/views")
public class AdminViewsRestControllerIT extends SpringIntegrationTest {
}
