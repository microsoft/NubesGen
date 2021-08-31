package io.github.nubesgen.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddonConfiguration {

    @JsonProperty("type")
    private AddonType addonType;

    @JsonProperty("tier")
    private Tier tier;

    public AddonConfiguration() {}

    public AddonConfiguration(AddonType addonType, Tier tier) {
        this.addonType = addonType;
        this.tier = tier;
    }

    public AddonType getAddonType() {
        return addonType;
    }

    public void setAddonType(AddonType addonType) {
        this.addonType = addonType;
    }

    public Tier getTier() {
        return tier;
    }

    public void setTier(Tier tier) {
        this.tier = tier;
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
        return ("AddonConfiguration{" + "addonType=" + addonType + ", tier=" + tier + '}');
    }
}
