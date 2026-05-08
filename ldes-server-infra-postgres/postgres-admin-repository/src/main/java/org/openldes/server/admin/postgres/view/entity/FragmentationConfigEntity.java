package org.openldes.server.admin.postgres.view.entity;

import java.util.Map;
import org.openldes.server.domain.model.FragmentationConfig;

public class FragmentationConfigEntity {
    private String name;
    private Map<String, String> config;

    public FragmentationConfigEntity() {}

    public FragmentationConfigEntity(String name, Map<String, String> config) {
        this.name = name;
        this.config = config;
    }

    public static FragmentationConfigEntity toEntity(FragmentationConfig fragmentationConfig) {
        return new FragmentationConfigEntity(fragmentationConfig.getName(), fragmentationConfig.getConfig());
    }

    public FragmentationConfig fromEntity() {
        var fragmentationConfig = new FragmentationConfig();
        fragmentationConfig.setName(this.name);
        fragmentationConfig.setConfig(this.config);
        return fragmentationConfig;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FragmentationConfigEntity that)) return false;

	    return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
