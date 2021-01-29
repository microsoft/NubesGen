package io.github.nubesgen.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApplicationConfiguration {

    @JsonProperty("type")
    private ApplicationType applicationType;

    @JsonProperty("tier")
    private Tier tier;

    public ApplicationConfiguration() {
        this.applicationType = ApplicationType.APP_SERVICE;
        this.tier = Tier.FREE;
    }

    public ApplicationConfiguration(ApplicationType applicationType, Tier tier) {
        this.applicationType = applicationType;
        this.tier = tier;
    }

    public ApplicationType getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(ApplicationType applicationType) {
        this.applicationType = applicationType;
    }

    public Tier getTier() {
        return tier;
    }

    public void setTier(Tier tier) {
        this.tier = tier;
    }

    @Override
    public String toString() {
        return "ApplicationConfiguration{" +
                "applicationType=" + applicationType +
                ", tier=" + tier +
                '}';
    }
}
