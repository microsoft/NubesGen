package io.github.nubesgen.service.telemetry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!azure")
public class NoopTelemetryService implements TelemetryService {

    private final Logger log = LoggerFactory.getLogger(NoopTelemetryService.class);

    @Override
    public void storeConfiguration(String configuration) {
        log.debug("Not sending any telemetry data");
    }
}
