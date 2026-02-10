package org.openldes.server.fragmentation.factory;

import org.openldes.server.domain.model.ViewName;

public interface RootBucketCreator {
	void createRootBucketForView(ViewName viewName);
}
