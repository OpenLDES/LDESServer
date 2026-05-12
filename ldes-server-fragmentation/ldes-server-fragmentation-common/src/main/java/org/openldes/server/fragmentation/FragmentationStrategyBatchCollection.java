package org.openldes.server.fragmentation;

import io.micrometer.observation.ObservationRegistry;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import org.openldes.server.domain.events.admin.EventStreamDeletedEvent;
import org.openldes.server.domain.events.admin.ViewAddedEvent;
import org.openldes.server.domain.events.admin.ViewDeletedEvent;
import org.openldes.server.domain.events.admin.ViewInitializationEvent;
import org.openldes.server.domain.events.admin.ViewSupplier;
import org.openldes.server.domain.model.ViewName;
import org.openldes.server.domain.model.ViewSpecification;
import org.openldes.server.fragmentation.factory.FragmentationStrategyCreator;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
public class FragmentationStrategyBatchCollection implements FragmentationStrategyCollection {

	private final Set<FragmentationStrategyBatchExecutor> fragmentationStrategySet;
	private final FragmentationStrategyCreator fragmentationStrategyCreator;
	private final ObservationRegistry observationRegistry;

	public FragmentationStrategyBatchCollection(
			FragmentationStrategyCreator fragmentationStrategyCreator,
			ObservationRegistry observationRegistry) {
		this.fragmentationStrategyCreator = fragmentationStrategyCreator;
		this.observationRegistry = observationRegistry;
		this.fragmentationStrategySet = new HashSet<>();
	}

	@Override
	public List<FragmentationStrategyBatchExecutor> getAllFragmentationStrategyExecutors(String collectionName) {
		return fragmentationStrategySet
				.stream()
				.filter(executor -> executor.isPartOfCollection(collectionName))
				.toList();
	}

	@Override
	public Optional<FragmentationStrategyBatchExecutor> getFragmentationStrategyExecutor(String viewName) {
		return fragmentationStrategySet.stream()
				.filter(fragmentationStrategyBatchExecutor ->
						fragmentationStrategyBatchExecutor.getViewName()
								.asString()
								.equals(viewName))
				.findFirst();
	}

	@EventListener({ViewAddedEvent.class, ViewInitializationEvent.class})
	@Order(1)
	public void handleViewAddedEvent(ViewSupplier event) {
		final var fragmentationStrategyExecutor = createExecutor(event.viewSpecification().getName(), event.viewSpecification());
		fragmentationStrategySet.add(fragmentationStrategyExecutor);
	}

	@EventListener
	public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
		removeFromStrategySet(
				executor -> Objects.equals(executor.getViewName().getCollectionName(), event.collectionName()));
	}

	@EventListener
	public void handleViewDeletedEvent(ViewDeletedEvent event) {
		removeFromStrategySet(executor -> Objects.equals(executor.getViewName(), event.getViewName()));
	}

	private void removeFromStrategySet(Predicate<FragmentationStrategyBatchExecutor> filterPredicate) {
		fragmentationStrategySet
				.stream()
				.filter(filterPredicate)
				.toList()
				.forEach(fragmentationStrategySet::remove);
	}

	private FragmentationStrategyBatchExecutor createExecutor(ViewName viewName, ViewSpecification viewSpecification) {
		final FragmentationStrategy fragmentationStrategy = fragmentationStrategyCreator
				.createFragmentationStrategyForView(viewSpecification);
		return new FragmentationStrategyBatchExecutor(viewName, fragmentationStrategy, observationRegistry);
	}
}
