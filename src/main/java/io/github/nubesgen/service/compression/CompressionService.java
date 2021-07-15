package io.github.nubesgen.service.compression;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public interface CompressionService {
    ByteArrayOutputStream compressApplication(Map<String, String> files) throws IOException;

    boolean isZip();
}
