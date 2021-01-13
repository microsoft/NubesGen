package io.github.nubesgen.service.compression;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ZipService implements CompressionService {

    @Override
    public ByteArrayOutputStream compressApplication(Map<String, String> files) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (String fileName : files.keySet()) {
                ZipEntry entry = new ZipEntry(fileName);
                zos.putNextEntry(entry);
                zos.write(files.get(fileName).getBytes());
                zos.closeEntry();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return baos;
    }

    @Override
    public boolean isZip() {
        return true;
    }
}
