package org.openldes.server;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Reproduction suite for <a href="https://github.com/OpenLDES/LDESServer/issues/48">issue 48</a>:
 * members that are ingested into a collection with more than one view are not found back in every view.
 * <p>
 * The scenarios in this suite are expected to fail for as long as the issue is not fixed, which is why they
 * live in their own suite instead of being added to {@link LdesServerFragmentationIT}.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/issue-48")
public class LdesServerMissingMembersIT extends LdesServerIntegrationTest {
}
