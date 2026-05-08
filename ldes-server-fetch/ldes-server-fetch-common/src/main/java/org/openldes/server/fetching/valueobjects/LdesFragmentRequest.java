package org.openldes.server.fetching.valueobjects;

import java.util.List;
import java.util.Objects;
import org.openldes.server.domain.model.ViewName;

public record LdesFragmentRequest(ViewName viewName, List<FragmentPair> fragmentPairs) {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else {
            if (o == null || getClass() != o.getClass()) {
                return false;
            } else {
                LdesFragmentRequest that = (LdesFragmentRequest) o;
                return Objects.equals(viewName, that.viewName) && Objects.equals(fragmentPairs, that.fragmentPairs);
            }
        }
    }

}
