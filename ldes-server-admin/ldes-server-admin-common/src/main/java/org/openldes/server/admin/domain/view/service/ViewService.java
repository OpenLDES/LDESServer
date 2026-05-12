package org.openldes.server.admin.domain.view.service;

import java.util.List;
import org.openldes.server.domain.model.ViewName;
import org.openldes.server.domain.model.ViewSpecification;

public interface ViewService {

	void addView(ViewSpecification viewSpecification);

	ViewSpecification getViewByViewName(ViewName viewName);

	List<ViewSpecification> getViewsByCollectionName(String collectionName);

	void deleteViewByViewName(ViewName viewName);
}
