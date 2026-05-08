package org.openldes.server.admin.postgres.dcatserver;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.openldes.server.admin.postgres.SpringIntegrationTest;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/serverdcat")
public class DcatServerRepositoryIT extends SpringIntegrationTest {
}
