package org.openldes.server.fragmentation.repository;

import java.util.List;
import org.openldes.server.fragmentation.entities.UnprocessedView;

public interface UnprocessedViewRepository {
	List<UnprocessedView> findAll();
}
