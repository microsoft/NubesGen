package io.github.nubesgen.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NetworkConfiguration {

    @JsonProperty("network")
    private NetworkType networkType;

    @JsonProperty("public")
    private PublicEndpointType publicEndpointType;

    public NetworkConfiguration(NetworkType networkType) {
        this(networkType, PublicEndpointType.NotPublic);
    }

    public NetworkConfiguration(NetworkType networkType, PublicEndpointType publicEndpointType) {
        this.networkType = networkType;
        this.publicEndpointType = publicEndpointType;
    }

    public NetworkType getNetworkType() {
        return this.networkType;
    }

    public PublicEndpointType getPublicEndpoint() {
        return publicEndpointType;
    }

    @Override
    public String toString() {
        return "NetworkConfiguration{" +
                "networkType=" + networkType +
                ", publicEndpointType=" + publicEndpointType +
                '}';
    }
}
