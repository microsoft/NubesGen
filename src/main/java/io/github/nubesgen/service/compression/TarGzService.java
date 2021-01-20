package io.github.nubesgen.service.compression;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.springframework.stereotype.Service;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@Service
public class TarGzService implements CompressionService {

    @Override
    public ByteArrayOutputStream compressApplication(Map<String, String> files) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(baos);
        ArchiveOutputStream archive = new TarArchiveOutputStream(gzOut);
        for (String fileName : files.keySet()) {
            TarArchiveEntry tarEntry = new TarArchiveEntry(fileName);
            byte[] data = files.get(fileName).getBytes();
            tarEntry.setSize(data.length);
            archive.putArchiveEntry(tarEntry);
            archive.write(data);
            archive.closeArchiveEntry();
        }
        archive.finish();
        gzOut.finish();
        return baos;
    }

    @Override
    public boolean isZip() {
        return false;
    }
}
