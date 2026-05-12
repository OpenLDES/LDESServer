package org.openldes.server.admin.domain.dcatdataset.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;

class DcatDatasetTest {

	@Test
	void when_CallingGetDatasetIriString_should_ReturnTheCorrectIriString() {
		String result = new DcatDataset("collectionName").getDatasetIriString("http://localhost.dev");

		Assertions.assertEquals("http://localhost.dev/collectionName", result);
	}

}
