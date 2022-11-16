package io.github.nubesgen.service;

import com.azure.storage.blob.BlobServiceAsyncClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * Send (anonymized) telemetry data when running on Nubesgen.com.
 */
@Service
public class TelemetryService {

    private final Logger log = LoggerFactory.getLogger(TelemetryService.class);

    @Value("${azure.storage.account-name}")
    private String storageAccountName;

    @Value("${azure.storage.account-key}")
    private String storageAccountKey;

    @Value("${azure.storage.blob-endpoint}")
    private String blobStorageEndpoint;

    private boolean telemetryEnabled;

    private BlobServiceAsyncClient blobServiceAsyncClient;

    @PostConstruct
    public void init() {
        if (storageAccountName == null || storageAccountName.isEmpty() || storageAccountKey == null || storageAccountKey.isEmpty()) {
            log.warn("Telemetry is disabled, as it was not configured");
            telemetryEnabled = false;
        } else {
            log.warn("Telemetry is enabled");
            telemetryEnabled = true;
            blobServiceAsyncClient =
                new BlobServiceClientBuilder()
                    .connectionString(
                        "DefaultEndpointsProtocol=https;AccountName=" +
                        storageAccountName +
                        ";AccountKey=" +
                        storageAccountKey +
                        ";EndpointSuffix=core.windows.net"
                    )
                    .buildAsyncClient();
        }
    }

    public void storeConfiguration(String configuration) {
        if (!telemetryEnabled) {
            return;
        }
        log.info("Sending telemetry data...");
        UUID uuid = UUID.randomUUID();
        String blobName = uuid + ".json";
        ByteBuffer data = StandardCharsets.UTF_8.encode(configuration);
        Flux<ByteBuffer> flux = Flux.just(data);
        blobServiceAsyncClient
            .getBlobContainerAsyncClient("stblobnubesgen001")
            .getBlobAsyncClient(blobName)
            .upload(flux, null)
            .doOnError(throwable -> log.info("Telemetry error", throwable))
            .subscribe();
    }
}
