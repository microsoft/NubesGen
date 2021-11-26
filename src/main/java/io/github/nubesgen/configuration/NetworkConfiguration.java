package io.github.nubesgen.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NetworkConfiguration {

    @JsonProperty("network")
    private NetworkType networkType;

    public NetworkConfiguration() {
        this.networkType = NetworkType.PUBLIC;
    }

    public NetworkConfiguration(NetworkType networkType) {
        this.networkType = networkType;
    }

    public NetworkType getNetworkType() {
        return this.networkType;
    }

    @Override
    public String toString() {
        return "NetworkConfiguration{" + "networkType=" + networkType + '}';
    }
}
