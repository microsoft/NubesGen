package io.github.nubesgen.configuration;

public class NetworkConfiguration {
    private NetworkType networkType;

    private PublicEndpointType publicEndpointType;


    public NetworkConfiguration(NetworkType networkType) {
        this(networkType, PublicEndpointType.NotPublic);
    }

    public NetworkConfiguration(NetworkType networkType, PublicEndpointType publicEndpointType) {
        this.networkType = networkType;
        this.publicEndpointType = publicEndpointType;
    }

    public NetworkType getNetworkType(){
        return this.networkType;
    }

    public PublicEndpointType getPublicEndpoint(){
        return publicEndpointType;
    }
}
