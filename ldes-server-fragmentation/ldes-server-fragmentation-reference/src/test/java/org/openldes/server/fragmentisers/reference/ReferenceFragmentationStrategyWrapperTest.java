package org.openldes.server.fragmentisers.reference;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;
import static org.openldes.server.fragmentisers.reference.ReferenceFragmentationStrategyWrapper.DEFAULT_FRAGMENTATION_KEY;
import static org.openldes.server.fragmentisers.reference.ReferenceFragmentationStrategyWrapper.DEFAULT_FRAGMENTATION_PATH;
import static org.openldes.server.fragmentisers.reference.ReferenceFragmentationStrategyWrapper.FRAGMENTATION_KEY;
import static org.openldes.server.fragmentisers.reference.ReferenceFragmentationStrategyWrapper.FRAGMENTATION_PATH;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openldes.server.domain.model.ConfigProperties;
import org.openldes.server.fragmentation.FragmentationStrategy;
import org.springframework.context.ApplicationContext;

class ReferenceFragmentationStrategyWrapperTest {

    private final ApplicationContext applicationContext = mock(ApplicationContext.class);
    private final FragmentationStrategy fragmentationStrategy = mock(FragmentationStrategy.class);
    private ReferenceFragmentationStrategyWrapper referenceFragmentationStrategyWrapper;

    @BeforeEach
    void setUp() {
        referenceFragmentationStrategyWrapper = new ReferenceFragmentationStrategyWrapper();
    }

    @Test
    void when_FragmentationStrategyIsUpdated_GeospatialFragmentationStrategyIsReturned() {
        ConfigProperties properties = new ConfigProperties(
                Map.of(FRAGMENTATION_PATH, DEFAULT_FRAGMENTATION_PATH, FRAGMENTATION_KEY, DEFAULT_FRAGMENTATION_KEY));
        FragmentationStrategy decoratedFragmentationStrategy = referenceFragmentationStrategyWrapper
                .wrapFragmentationStrategy(applicationContext, fragmentationStrategy, properties);
        assertInstanceOf(ReferenceFragmentationStrategy.class, decoratedFragmentationStrategy);
    }

}
