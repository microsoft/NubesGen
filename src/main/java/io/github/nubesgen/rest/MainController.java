package io.github.nubesgen.rest;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/")
public class MainController {

    private final Logger log = LoggerFactory.getLogger(MainController.class);

    @GetMapping(value = "/")
    public @ResponseBody ResponseEntity<byte[]> generateApplication() {
        String zippedApplication;
        try {
            zippedApplication = ""; // TODO
        } catch (Exception e) {
            log.error("Error generating application", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(zippedApplication);
            byte[] out = IOUtils.toByteArray(inputStream);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add("content-disposition", "attachment; filename=nubesgen.zip");
            responseHeaders.add("Content-Type", "application/octet-stream");
            responseHeaders.add("Content-Transfer-Encoding", "binary");
            responseHeaders.add("Content-Length", String.valueOf(out.length));
            return new ResponseEntity<>(out, responseHeaders, HttpStatus.OK);
        } catch (IOException ioe) {
            log.error("Error sending zipped application", ioe);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
