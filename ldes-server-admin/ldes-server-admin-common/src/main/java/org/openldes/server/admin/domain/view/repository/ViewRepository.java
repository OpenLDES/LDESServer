package org.openldes.server.admin.domain.view.repository;

import org.openldes.server.domain.model.ViewName;
import org.openldes.server.domain.model.ViewSpecification;

import java.util.List;
import java.util.Optional;

public interface ViewRepository {
	List<ViewSpecification> retrieveAllViews();

	void saveView(ViewSpecification viewSpecification);

	void deleteViewByViewName(ViewName viewName);

	Optional<ViewSpecification> getViewByViewName(ViewName viewName);

	List<ViewSpecification> retrieveAllViewsOfCollection(String collectionName);
}
