package be.ugent.idlab.knows.dataio.access;

import be.ugent.idlab.knows.dataio.compression.Compression;
import be.ugent.idlab.knows.dataio.cores.LocalAccessTestCore;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assume;
import org.junit.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class LocalAccessTest extends LocalAccessTestCore {

    @Test
    public void relativePath() throws IOException {
        String relative = "src/test/resources/local_access/file.txt";

        Access access = new LocalFileAccess("", relative, "txt", StandardCharsets.UTF_8);
        String expected = getInput(Path.of(relative), StandardCharsets.UTF_8);
        String actual = getInput(access);

        assertEquals(expected, actual);
    }

    @Test
    public void absolutePath() throws IOException {
        String relative = "src/test/resources/local_access/file.txt";
        String absolute = new File(relative).getAbsolutePath();
        Access access = new LocalFileAccess(absolute, "", "txt", StandardCharsets.UTF_8);

        String expected = getInput(Path.of(absolute), StandardCharsets.UTF_8);
        String actual = getInput(access);

        assertEquals(expected, actual);
    }

    @Test
    public void relativeToBase() throws IOException {
        String base = "src/test/resources/";
        String baseAbsolute = new File(base).getAbsolutePath();
        Access access = new LocalFileAccess("local_access/file.txt", baseAbsolute, "txt", StandardCharsets.UTF_8);

        String expected = getInput(Path.of(baseAbsolute, "local_access/file.txt"), StandardCharsets.UTF_8);
        String actual = getInput(access);

        assertEquals(expected, actual);
    }

    @Test
    public void nonExistentFile() {
        Access access = new LocalFileAccess("", "not_existing_file.csv", "csv", StandardCharsets.UTF_8);
        assertThrows(NoSuchFileException.class, access::getInputStream);
    }

    @EnabledOnOs(OS.WINDOWS)
    @Test
    public void accessFileRelative_csv_0000_windows() throws Exception {
        Assume.assumeTrue(SystemUtils.IS_OS_WINDOWS);
        String relative = "src\\test\\resources\\csv\\0000.csv";
        Access access = new LocalFileAccess("", relative, "csv", StandardCharsets.UTF_8);
        try {
            assertEquals(readWithUTF8(Path.of(relative)), getResultUTF8(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @EnabledOnOs(OS.WINDOWS)
    @Test
    public void accessFileRelative_csv_0001_windows() throws Exception {
        Assume.assumeTrue(SystemUtils.IS_OS_WINDOWS);
        String relative = "src\\test\\resources\\csv\\0001.csv";

        Access access = new LocalFileAccess("", relative, "csv", StandardCharsets.UTF_8);
        try {
            assertEquals(readWithUTF8(Path.of(relative)), getResultUTF8(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void compressionGZip() throws SQLException, IOException, ParserConfigurationException, TransformerException {
        Access access = new LocalFileAccess("access/compression/Friends.json.gz", "src/test/resources", "gzip", Charset.defaultCharset(), Compression.GZip);
        String expected = """
                [
                  {\s
                    "id": 0,
                    "name": "Monica Geller",
                    "age": 33
                  },
                  {\s
                    "id": 1,
                    "name": "Rachel Green",
                    "age": 34
                  },
                  {\s
                    "id": 2,
                    "name": "Joey Tribbiani",
                    "age": 35
                  },
                  {\s
                    "id": 3,
                    "name": "Chandler Bing",
                    "age": 36
                  },
                  {\s
                    "id": 4,
                    "name": "Ross Geller",
                    "age": 37
                  }
                ]
                """;

        String actual = new String(access.getInputStream().readAllBytes());
        assertEquals(expected, actual);
    }

    @Test
    public void compressionZIP() throws SQLException, IOException, ParserConfigurationException, TransformerException {
        Access access = new LocalFileAccess("access/compression/Friends.json.zip", "src/test/resources", "zip", Charset.defaultCharset(), Compression.Zip);
        String expected = """
                [
                  {\s
                    "id": 0,
                    "name": "Monica Geller",
                    "age": 33
                  },
                  {\s
                    "id": 1,
                    "name": "Rachel Green",
                    "age": 34
                  },
                  {\s
                    "id": 2,
                    "name": "Joey Tribbiani",
                    "age": 35
                  },
                  {\s
                    "id": 3,
                    "name": "Chandler Bing",
                    "age": 36
                  },
                  {\s
                    "id": 4,
                    "name": "Ross Geller",
                    "age": 37
                  }
                ]
                """;
        String actual = new String(access.getInputStream().readAllBytes());
        assertEquals(expected, actual);

    }

    @Test
    public void compressionTarGZ() throws SQLException, IOException, ParserConfigurationException, TransformerException {
        Access access = new LocalFileAccess("access/compression/Friends.json.tar.gz", "src/test/resources", "tar.gz", Charset.defaultCharset(), Compression.TarGZ);
        String expected = """
                [
                  {\s
                    "id": 0,
                    "name": "Monica Geller",
                    "age": 33
                  },
                  {\s
                    "id": 1,
                    "name": "Rachel Green",
                    "age": 34
                  },
                  {\s
                    "id": 2,
                    "name": "Joey Tribbiani",
                    "age": 35
                  },
                  {\s
                    "id": 3,
                    "name": "Chandler Bing",
                    "age": 36
                  },
                  {\s
                    "id": 4,
                    "name": "Ross Geller",
                    "age": 37
                  }
                ]
                """;
        String actual = new String(access.getInputStream().readAllBytes());
        assertEquals(expected, actual);
    }

    @Test
    public void compressionTarXZ() throws SQLException, IOException, ParserConfigurationException, TransformerException {
        Access access = new LocalFileAccess("access/compression/Friends.json.tar.xz", "src/test/resources", "tar.xz", Charset.defaultCharset(), Compression.TarXZ);
        String expected = """
                [
                  {\s
                    "id": 0,
                    "name": "Monica Geller",
                    "age": 33
                  },
                  {\s
                    "id": 1,
                    "name": "Rachel Green",
                    "age": 34
                  },
                  {\s
                    "id": 2,
                    "name": "Joey Tribbiani",
                    "age": 35
                  },
                  {\s
                    "id": 3,
                    "name": "Chandler Bing",
                    "age": 36
                  },
                  {\s
                    "id": 4,
                    "name": "Ross Geller",
                    "age": 37
                  }
                ]
                """;
        String actual = new String(access.getInputStream().readAllBytes());
        assertEquals(expected, actual);
    }
}
