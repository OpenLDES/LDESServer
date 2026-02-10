package org.openldes.server.admin.domain.view.service;

import org.openldes.server.domain.model.DcatView;
import org.openldes.server.domain.model.ViewName;
import org.apache.jena.rdf.model.Model;

import java.util.List;
import java.util.Optional;

public interface DcatViewService {

	void create(ViewName viewName, Model dcat);

	Optional<DcatView> findByViewName(ViewName viewName);

	void update(ViewName viewName, Model dcat);

	void delete(ViewName viewName);

	List<DcatView> findAll();
}
