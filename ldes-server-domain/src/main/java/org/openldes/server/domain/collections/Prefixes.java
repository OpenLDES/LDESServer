package org.openldes.server.domain.collections;

import java.util.Map;

@FunctionalInterface
public interface Prefixes {
	Map<String, String> getPrefixes();
}
