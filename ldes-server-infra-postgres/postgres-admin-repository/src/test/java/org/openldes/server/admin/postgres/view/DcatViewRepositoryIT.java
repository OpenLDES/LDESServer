package org.openldes.server.admin.postgres.view;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.openldes.server.admin.postgres.SpringIntegrationTest;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/view")
public class DcatViewRepositoryIT extends SpringIntegrationTest {
}
