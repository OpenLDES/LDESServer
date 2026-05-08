package org.openldes.server.compaction.batch;

import java.util.Map;
import java.util.stream.Collectors;
import org.openldes.server.compaction.domain.repository.ViewCollection;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

@Component
public class ViewCapacityPartitioner implements Partitioner {
	private final ViewCollection viewCollection;

	public ViewCapacityPartitioner(ViewCollection viewCollection) {
		this.viewCollection = viewCollection;
	}

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		return viewCollection.getAllViewCapacities().stream()
				.collect(Collectors.toMap(
						viewCapacity -> "view:%s".formatted(viewCapacity.getViewName().asString()),
						viewCapacity -> new ExecutionContext(Map.of(
								"viewName", viewCapacity.getViewName().asString(),
								"capacityPerPage", viewCapacity.getCapacityPerPage()
						))
				));
	}
}
