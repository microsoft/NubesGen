package io.github.nubesgen.configuration;

/**
 * How the application is exposed to the Internet
 * AFD = Azure Front Door
 * GW = Azure Application Gateway. Not yet supported
 */
public enum PublicEndpointType {
    NotPublic,
    AFD,
    GW
}
