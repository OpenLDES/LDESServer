package org.openldes.server.admin.postgres.shaclshape.mapper;

import org.openldes.server.admin.domain.shacl.entities.ShaclShape;
import org.openldes.server.admin.postgres.shaclshape.entity.ShaclShapeEntity;

public class ShaclShapeMapper {
    private ShaclShapeMapper() {}

    public static ShaclShape fromEntity(ShaclShapeEntity entity) {
        return new ShaclShape(entity.getCollectionName(), entity.getModel());
    }
}
