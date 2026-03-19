package org.openldes.server.domain.collections;

import org.openldes.server.domain.constants.WellKnownPrefix;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class WellKnownPrefixes implements Prefixes {
	@Override
	public Map<String, String> getPrefixes() {
		return Arrays.stream(WellKnownPrefix.values()).collect(
				Collectors.toMap(WellKnownPrefix::getPrefix, WellKnownPrefix::getUri)
		);
	}
}
