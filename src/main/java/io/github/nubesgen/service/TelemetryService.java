package io.github.nubesgen.service;

import com.azure.storage.blob.BlobServiceAsyncClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@Profile("azure")
public class TelemetryService {

    private final Logger log = LoggerFactory.getLogger(TelemetryService.class);

    private final BlobServiceAsyncClient blobServiceAsyncClient;

    public TelemetryService(BlobServiceClientBuilder blobServiceClientBuilder) {
        blobServiceAsyncClient = blobServiceClientBuilder.buildAsyncClient();
    }

    public void storeConfiguration(String configuration) {
        log.info("Sending telemetry data...");
        UUID uuid = UUID.randomUUID();
        String blobName = uuid + ".json";
        ByteBuffer data = StandardCharsets.UTF_8.encode(configuration);
        Flux<ByteBuffer> flux = Flux.just(data);
        blobServiceAsyncClient
                .getBlobContainerAsyncClient("stblobnubesgen001")
                .getBlobAsyncClient(blobName)
                .upload(flux, null)
                .doOnError(
                        throwable -> log.info("Telemetry error", throwable)
                )
                .subscribe();
    }
}
