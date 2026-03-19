package org.openldes.server.admin.postgres.dcatdataservice.mapper;

import org.openldes.server.admin.postgres.dcatdataservice.entity.DcatDataServiceEntity;
import org.openldes.server.domain.model.DcatView;
import org.openldes.server.domain.model.ViewName;

public class DcatViewMapper {
    private DcatViewMapper() {
    }

    public static DcatView fromEntity(DcatDataServiceEntity entity) {
        return DcatView.from(
                ViewName.fromString(entity.getViewName()),
                entity.getModel()
        );
    }
}
