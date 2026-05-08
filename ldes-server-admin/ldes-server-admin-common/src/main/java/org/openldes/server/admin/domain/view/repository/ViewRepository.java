package org.openldes.server.admin.domain.view.repository;

import java.util.List;
import java.util.Optional;
import org.openldes.server.domain.model.ViewName;
import org.openldes.server.domain.model.ViewSpecification;

public interface ViewRepository {
	List<ViewSpecification> retrieveAllViews();

	void saveView(ViewSpecification viewSpecification);

	void deleteViewByViewName(ViewName viewName);

	Optional<ViewSpecification> getViewByViewName(ViewName viewName);

	List<ViewSpecification> retrieveAllViewsOfCollection(String collectionName);
}
