package org.openldes.server.domain.collections;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.openldes.server.domain.constants.WellKnownPrefix;
import org.springframework.stereotype.Component;

@Component
public class WellKnownPrefixes implements Prefixes {
	@Override
	public Map<String, String> getPrefixes() {
		return Arrays.stream(WellKnownPrefix.values()).collect(
				Collectors.toMap(WellKnownPrefix::getPrefix, WellKnownPrefix::getUri)
		);
	}
}
