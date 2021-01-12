package io.github.nubesgen.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("azure")
public class TelemetryService {

    private final Logger log = LoggerFactory.getLogger(TelemetryService.class);


}
