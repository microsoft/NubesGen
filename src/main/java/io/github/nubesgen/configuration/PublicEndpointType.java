package io.github.nubesgen.configuration;

/**
 * How the application is exposed to the Internet.
 *
 * FRONT_DOOR = Azure Front Door
 * APPLICATION_GATEWAY = Azure Application Gateway. Not yet supported
 */
public enum PublicEndpointType {
    PRIVATE,
    FRONTDOOR,
    APPLICATION_GATEWAY,
}
