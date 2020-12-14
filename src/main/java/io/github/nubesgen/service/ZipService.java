package io.github.nubesgen.service;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ZipService {

    public ByteArrayOutputStream zipApplication(Map<String, String> files) {
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
}
