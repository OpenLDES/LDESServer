package org.openldes.server;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Regression suite for <a href="https://github.com/OpenLDES/LDESServer/issues/52">issue 52</a>:
 * compaction grouped pages that are not adjacent in the chain and rewired the relations into a cycle, which left
 * most of the view unreachable from its root page.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/issue-52")
public class LdesServerCompactionChainIT extends LdesServerIntegrationTest {
}
