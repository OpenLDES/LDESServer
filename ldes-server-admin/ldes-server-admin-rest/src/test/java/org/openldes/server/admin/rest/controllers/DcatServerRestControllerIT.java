package org.openldes.server.admin.rest.controllers;

import org.openldes.server.admin.rest.config.SpringIntegrationTest;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/dcatserver")
public class DcatServerRestControllerIT extends SpringIntegrationTest {
}
