package be.ugent.idlab.knows.dataio.access;

import be.ugent.idlab.knows.dataio.cores.LocalAccessTestCore;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assume;
import org.junit.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.NoSuchFileException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class LocalAccessTest extends LocalAccessTestCore {

    @Test
    public void relativePath() throws IOException, SQLException {
        String relative = "src/test/resources/local_access/file.txt";

        Access access = new LocalFileAccess("", relative, "txt", "utf-8");
        String expected = getInput(Path.of(relative), StandardCharsets.UTF_8);
        String actual = getInput(access);

        assertEquals(expected, actual);
    }

    @Test
    public void absolutePath() throws IOException, SQLException {
        String relative = "src/test/resources/local_access/file.txt";
        String absolute = new File(relative).getAbsolutePath();
        Access access = new LocalFileAccess(absolute, "", "txt", "utf-8");

        String expected = getInput(Path.of(absolute), StandardCharsets.UTF_8);
        String actual = getInput(access);

        assertEquals(expected, actual);
    }

    @Test
    public void badEncoding() {
        String path = "src/test/resources/local_access/file.txt";

        assertThrows(IllegalArgumentException.class,
                () -> new LocalFileAccess("", path, "txt", "bogus"));
    }

    @Test
    public void relativeToBase() throws IOException, SQLException {
        String base = "src/test/resources/";
        String baseAbsolute = new File(base).getAbsolutePath();
        Access access = new LocalFileAccess("local_access/file.txt", baseAbsolute, "txt", "utf-8");

        String expected = getInput(Path.of(baseAbsolute, "local_access/file.txt"), StandardCharsets.UTF_8);
        String actual = getInput(access);

        assertEquals(expected, actual);
    }

    @Test
    public void nonExistentFile() {
        Access access = new LocalFileAccess("", "not_existing_file.csv", "csv", "utf-8");
        assertThrows(NoSuchFileException.class, access::getInputStream);
    }

    @EnabledOnOs(OS.WINDOWS)
    @Test
    public void accessFileRelative_csv_0000_windows() throws Exception {
        Assume.assumeTrue(SystemUtils.IS_OS_WINDOWS);
        String relative = "src\\test\\resources\\csv\\0000.csv";
        Access access = new LocalFileAccess("", relative, "csv", "utf-8");
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

        Access access = new LocalFileAccess("", relative, "csv", "utf-8");
        try {
            assertEquals(readWithUTF8(Path.of(relative)), getResultUTF8(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }
}
