package org.openldes.server.admin.postgres.dcatdataset.mapper;

import org.openldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import org.openldes.server.admin.postgres.dcatdataset.entity.DcatDatasetEntity;

public class DcatDatasetMapper {
    private DcatDatasetMapper() {
    }

    public static DcatDataset fromEntity(DcatDatasetEntity entity) {
        return new DcatDataset(entity.getCollectionName(), entity.getModel());
    }
}
