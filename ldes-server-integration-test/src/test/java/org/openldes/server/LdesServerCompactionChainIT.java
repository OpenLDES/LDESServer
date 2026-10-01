package org.openldes.server;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Reproduction suite for <a href="https://github.com/OpenLDES/LDESServer/issues/52">issue 52</a>:
 * compaction groups pages that are not adjacent in the chain and rewires the relations into a cycle, which leaves
 * most of the view unreachable from its root page.
 * <p>
 * The scenario in this suite is expected to fail for as long as the issue is not fixed, which is why it lives in its
 * own suite instead of being added to {@link MaintenanceIT}.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/issue-52")
public class LdesServerCompactionChainIT extends LdesServerIntegrationTest {
}
