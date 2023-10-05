package io.github.nubesgen.cli.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.NoSuchFileException;

import org.junit.jupiter.api.Test;
class FilesUtilTest {

    @Test
    public void readStringOk() throws URISyntaxException, IOException {
        final Path path = Paths.get(getClass().getResource("/util/content.txt").toURI());
        final String content = FilesUtil.readString(path);
        assertThat(content).isEqualTo("Nubes\nGen\n");
    }

    @Test
    public void readStringNonExistentThrowsFileNotFoundException() throws URISyntaxException, IOException {
        final Path path = Paths.get("nonexistent");
        assertThatThrownBy(() -> FilesUtil.readString(path)).isInstanceOf(NoSuchFileException.class);
    }

}
