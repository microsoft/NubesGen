package io.github.nubesgen.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddonConfiguration {

    @JsonProperty("type")
    private AddonType addonType;

    @JsonProperty("size")
    private ConfigurationSize configurationSize;

    public AddonConfiguration() {
    }

    public AddonConfiguration(AddonType addonType, ConfigurationSize configurationSize) {
        this.addonType = addonType;
        this.configurationSize = configurationSize;
    }

    public AddonType getAddonType() {
        return addonType;
    }

    public void setAddonType(AddonType addonType) {
        this.addonType = addonType;
    }

    public ConfigurationSize getConfigurationSize() {
        return configurationSize;
    }

    public void setConfigurationSize(ConfigurationSize configurationSize) {
        this.configurationSize = configurationSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AddonConfiguration that = (AddonConfiguration) o;

        return addonType == that.addonType;
    }

    @Override
    public int hashCode() {
        return addonType.hashCode();
    }

    @Override
    public String toString() {
        return "AddonConfiguration{" +
                "addonType=" + addonType +
                ", configurationSize=" + configurationSize +
                '}';
    }
}
