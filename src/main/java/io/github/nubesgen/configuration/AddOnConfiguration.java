package io.github.nubesgen.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddOnConfiguration {

    @JsonProperty("type")
    private AddOnType addOnType;

    @JsonProperty("size")
    private ConfigurationSize configurationSize;

    public AddOnConfiguration() {
    }

    public AddOnConfiguration(AddOnType addOnType, ConfigurationSize configurationSize) {
        this.addOnType = addOnType;
        this.configurationSize = configurationSize;
    }

    public AddOnType getAddOnType() {
        return addOnType;
    }

    public void setAddOnType(AddOnType addOnType) {
        this.addOnType = addOnType;
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

        AddOnConfiguration that = (AddOnConfiguration) o;

        return addOnType == that.addOnType;
    }

    @Override
    public int hashCode() {
        return addOnType.hashCode();
    }

    @Override
    public String toString() {
        return "AddOnConfiguration{" +
                "addOnType=" + addOnType +
                ", configurationSize=" + configurationSize +
                '}';
    }
}
