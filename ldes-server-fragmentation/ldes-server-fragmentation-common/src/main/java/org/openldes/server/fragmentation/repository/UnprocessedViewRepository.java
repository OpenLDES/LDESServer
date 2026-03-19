package org.openldes.server.fragmentation.repository;

import org.openldes.server.fragmentation.entities.UnprocessedView;

import java.util.List;

public interface UnprocessedViewRepository {
	List<UnprocessedView> findAll();
}
