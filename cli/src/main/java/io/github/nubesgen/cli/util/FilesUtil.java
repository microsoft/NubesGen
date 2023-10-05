package io.github.nubesgen.cli.util;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Java 8 compatible Files helpers.
 */
public class FilesUtil {

    /**
     * Reads all content from a file into a string, decoding from bytes to characters using the UTF-8 charset. 
     * The method ensures that the file is closed when all content have been read or an I/O error, or other 
     * runtime exception, is thrown.
     * 
     * Reproduces the behavior of Java 11 Paths.readString(Path).
     * 
     * @param path the path to the file
     * @return a String containing the content read from the file
     * @throws IOException if an I/O error occurs reading from the file or a malformed or unmappable byte sequence is read
     */
    public static String readString(final Path path) throws IOException {
        final StringBuilder resultStringBuilder = new StringBuilder();
        try (final BufferedReader reader = Files.newBufferedReader(path, UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }
    
}
