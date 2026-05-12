package org.openldes.server.admin.domain.view.repository;

import java.util.List;
import java.util.Optional;
import org.openldes.server.domain.model.DcatView;
import org.openldes.server.domain.model.ViewName;

public interface DcatViewRepository {

    void save(DcatView dcatView);

    Optional<DcatView> findByViewName(ViewName viewName);

    void delete(ViewName viewName);

    List<DcatView> findAll();

}
