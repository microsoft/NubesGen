package io.github.nubesgen.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DatabaseConfiguration {

    @JsonProperty("type")
    private DatabaseType databaseType;

    @JsonProperty("tier")
    private Tier tier;

    public DatabaseConfiguration() {
        this.databaseType = DatabaseType.NONE;
        this.tier = Tier.FREE;
    }

    public DatabaseConfiguration(DatabaseType databaseType, Tier tier) {
        this.databaseType = databaseType;
        this.tier = tier;
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    public Tier getTier() {
        return tier;
    }

    public void setTier(Tier tier) {
        this.tier = tier;
    }

    @Override
    public String toString() {
        return "DatabaseConfiguration{" +
                "databaseType=" + databaseType +
                ", tier=" + tier +
                '}';
    }
}
