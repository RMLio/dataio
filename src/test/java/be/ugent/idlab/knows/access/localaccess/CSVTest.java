package be.ugent.idlab.knows.access.localaccess;

import be.ugent.idlab.knows.access.Access;
import be.ugent.idlab.knows.access.LocalFileAccess;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assume;
import org.junit.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.SQLException;
import static be.ugent.idlab.knows.access.localaccess.LocalAccessTest.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CSVTest {

    @Test(expected = FileNotFoundException.class)
    public void expect_error() throws SQLException, IOException, ClassNotFoundException {
        Access access = new LocalFileAccess("", "not_existing_file.csv", "csv", "utf-8");
        access.getInputStream();
    }

    @Test
    public void accessFileRelative_csv_0000_mightBeUTF8() throws Exception {
        String relative = "src/test/resources/csv/0000.csv";

        Access access = new LocalFileAccess("",relative, "csv", "utf-8");
        try {
            assertTrue(mightBeUTF8(access.getInputStream()));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileRelative_csv_0001_mightBeUTF8() throws Exception {
        String relative = "src/test/resources/csv/0001.csv";

        Access access = new LocalFileAccess("",relative, "csv", "utf-8");
        try {
            assertTrue(mightBeUTF8(access.getInputStream()));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileRelative_csv_0000_isUTF8() throws Exception {
        String relative = "src/test/resources/csv/0000.csv";

        Access access = new LocalFileAccess("",relative, "csv", "utf-8");
        try {
            assertTrue(isUTF8(access.getInputStream()));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileRelative_csv_0001_isUTF8() throws Exception {
        String relative = "src/test/resources/csv/0001.csv";

        Access access = new LocalFileAccess("",relative, "csv", "utf-8");
        try {
            assertTrue(isUTF8(access.getInputStream()));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileRelative_csv_0000_utf8() throws Exception {
        String relative = "src/test/resources/csv/0000.csv";

        Access access = new LocalFileAccess("",relative, "csv", "utf-8");
        try {
            assertEquals(readWithUTF8(Path.of(relative)), getResultUTF8(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileRelative_csv_0001_utf8() throws Exception {
        String relative = "src/test/resources/csv/0001.csv";

        Access access = new LocalFileAccess("",relative, "csv", "utf-8");
        try {
            assertEquals(readWithUTF8(Path.of(relative)), getResultUTF8(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileAbsolute_csv_0000_utf8() throws Exception {
        String relative = "src/test/resources/csv/0000.csv";
        String absolute = new File(relative).getAbsolutePath();

        Access access = new LocalFileAccess(absolute,"", "csv", "utf-8");
        try {
            assertEquals(readWithUTF8(Path.of(absolute)), getResultUTF8(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileAbsolute_csv_0001_utf8() throws Exception {
        String relative = "src/test/resources/csv/0001.csv";
        String absolute = new File(relative).getAbsolutePath();

        Access access = new LocalFileAccess(absolute,"", "csv", "utf-8");
        try {
            assertEquals(readWithUTF8(Path.of(absolute)), getResultUTF8(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileAbsolute_csv_0000() throws Exception {
        String relative = "src/test/resources/csv/0000.csv";
        String absolute = new File(relative).getAbsolutePath();

        Access access = new LocalFileAccess(absolute,"", "csv", "utf-8");
        try {
            assertEquals(readWithUTF8(Path.of(absolute)), getResultInputStream(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }

    }

    @Test
    public void accessFileAbsolute_csv_0001() throws Exception {
        String relative = "src/test/resources/csv/0001.csv";
        String absolute = new File(relative).getAbsolutePath();

        Access access = new LocalFileAccess(absolute,"", "csv", "utf-8");
        try {
            assertEquals(readWithUTF8(Path.of(absolute)), getResultInputStream(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileAbsolute_BOM_csv_0002() throws Exception {
        String relative = "src/test/resources/csv/0002_BOM.csv";
        String absolute = new File(relative).getAbsolutePath();

        Access access = new LocalFileAccess(absolute,"", "csv", "utf-8");
        try {
            assertEquals(readWithUTF8(Path.of(absolute)), getResultInputStream(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }

    }

    @EnabledOnOs(OS.WINDOWS)
    @Test
    public void accessFileRelative_csv_0000_windows() throws Exception {
        Assume.assumeTrue(SystemUtils.IS_OS_WINDOWS);
        String relative = "src\\test\\resources\\csv\\0000.csv";
        Access access = new LocalFileAccess("",relative, "csv", "utf-8");
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

        Access access = new LocalFileAccess("",relative, "csv", "utf-8");
        try {
            assertEquals(readWithUTF8(Path.of(relative)), getResultUTF8(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }
}
