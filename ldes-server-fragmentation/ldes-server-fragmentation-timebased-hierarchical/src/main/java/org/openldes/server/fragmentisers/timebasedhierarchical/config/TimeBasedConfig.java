package org.openldes.server.fragmentisers.timebasedhierarchical.config;

import org.openldes.server.fragmentisers.timebasedhierarchical.constants.Granularity;

public class TimeBasedConfig {
	private final String fragmenterSubjectFilter;
	private final String fragmentationPath;
	private final Granularity maxGranularity;

	public TimeBasedConfig(String fragmenterSubjectFilter, String fragmentationPath, Granularity maxGranularity) {
		this.fragmenterSubjectFilter = fragmenterSubjectFilter;
		this.fragmentationPath = fragmentationPath;
		this.maxGranularity = maxGranularity;
	}

	public String getFragmentationPath() {
		return fragmentationPath;
	}

	public String getFragmenterSubjectFilter() {
		return fragmenterSubjectFilter;
	}

	public Granularity getMaxGranularity() {
		return maxGranularity;
	}
}
