# Telemetry

As a lot of products, we use telemetry to inform our product strategy. We have two main tools to gather that telemetry: Microsoft Clarity and internal logs.

## Microsoft Clarity

We use [Microsoft Clarity](https://clarity.microsoft.com) to get general statistics, heatmaps and other product stats. While Clarity [may require the use of cookies](https://docs.microsoft.com/clarity/cookie-consent), we have disabled this feature. This limits the type of data we got (most notably around user sessions), but gives you a cookie-banner-free experience :angel:.

Clarity is installed both on the main site and our documentation site.

## NubesGen internal logging

When you call the nubesgen.com api (either through the CLI, the web interface or via a REST call), we will log the parameters sent to us as a json file in a private Azure Blob. 

We store the parameters sent to the API. This include the region, runtime, database type, required add-ons. The stored JSON looks like this: 

```json
{"date":"2021-05-06T09:17:22.784+00:00","region":"eastus","runtime":"DOCKER","application":{"type":"APP_SERVICE","tier":"FREE"},"database":{"type":"NONE","tier":"FREE"},"gitops":false,"addons":[]}
```

We do **not** store your project name, or your IP address.

The code managing telemetry is located in the [`TelemetryService`](https://github.com/microsoft/NubesGen/blob/main/rest-server/src/main/java/io/github/nubesgen/service/TelemetryService.java).

::: tip DISABLING TELEMETRY
We do not have _yet_ an option to opt-out from telemetry we collect. We would love to offer this option in the future. 
If you want to contribute, do not hesitate to reach out to us on [this issue](https://github.com/microsoft/NubesGen/issues/259)
:::

## 🍪 Cookies

NubesGen.com website does **not** use cookies to store Personally Identifiable Information. In future versions, we might use
