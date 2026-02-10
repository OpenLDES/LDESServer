package org.openldes.server.fragmentation;

import java.util.List;
import java.util.Optional;

public interface FragmentationStrategyCollection {
	List<FragmentationStrategyBatchExecutor> getAllFragmentationStrategyExecutors(String collectionName);
	Optional<FragmentationStrategyBatchExecutor> getFragmentationStrategyExecutor(String viewName);
}
