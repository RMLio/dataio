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
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import static be.ugent.idlab.knows.access.localaccess.LocalAccessTest.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CSVWTest {

    @Test
    public void accessFileRelative_csvw_0000_mightBeUTF8() throws Exception{
        String relative = "src/test/resources/csvw/0000_trim.csv";

        Access access = new LocalFileAccess("",relative, "csvw", "utf-8");
        try {
            assertTrue(mightBeUTF8(access.getInputStream()));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileRelative_csvw_0001_mightBeUTF8() throws Exception{
        String relative = "src/test/resources/csvw/1000_nulls.csv";

        Access access = new LocalFileAccess("",relative, "csvw", "utf-8");
        try {
            assertTrue(mightBeUTF8(access.getInputStream()));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }
    @Test
    public void accessFileRelative_csvw_0000_isUTF8() throws Exception{
        String relative = "src/test/resources/csvw/0000_trim.csv";

        Access access = new LocalFileAccess("",relative, "csvw", "utf-8");
        try {
            assertTrue(isUTF8(access.getInputStream()));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileRelative_csvw_0001_isUTF8() throws Exception{
        String relative = "src/test/resources/csvw/1000_nulls.csv";

        Access access = new LocalFileAccess("",relative, "csvw", "utf-8");
        try {
            assertTrue(isUTF8(access.getInputStream()));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileRelative_csvw_0000_utf8() throws Exception {
        String relative = "src/test/resources/csvw/0000_trim.csv";

        Access access = new LocalFileAccess("",relative, "csvw", "utf-8");
        try {
            assertEquals(readWithUTF8(Path.of(relative)), getResultUTF8(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileRelative_csvw_1000_utf8() throws Exception {
        String relative = "src/test/resources/csvw/1000_nulls.csv";

        Access access = new LocalFileAccess("",relative, "csvw", "utf-8");
        try {
            assertEquals(readWithUTF8(Path.of(relative)), getResultUTF8(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileAbsolute_csvw_0000_utf8() throws Exception {
        String relative = "src/test/resources/csvw/0000_trim.csv";
        String absolute = new File(relative).getAbsolutePath();

        Access access = new LocalFileAccess(absolute,"", "csvw", "utf-8");
        try {
            assertEquals(readWithUTF8(Path.of(absolute)), getResultUTF8(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileAbsolute_csvw_1000_utf8() throws Exception {
        String relative = "src/test/resources/csvw/1000_nulls.csv";
        String absolute = new File(relative).getAbsolutePath();

        Access access = new LocalFileAccess(absolute,"", "csvw", "utf-8");
        try {
            assertEquals(readWithUTF8(Path.of(absolute)), getResultUTF8(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileAbsolute_csvw_0000() throws Exception {
        String relative = "src/test/resources/csvw/0000_trim.csv";
        String absolute = new File(relative).getAbsolutePath();

        Access access = new LocalFileAccess(absolute,"", "csvw", "utf-8");
        try {
            assertEquals(readWithUTF8(Path.of(absolute)), getResultInputStream(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileAbsolute_csvw_1000() throws Exception {
        String relative = "src/test/resources/csvw/1000_nulls.csv";
        String absolute = new File(relative).getAbsolutePath();

        Access access = new LocalFileAccess(absolute,"", "csvw", "utf-8");
        try {
            assertEquals(readWithUTF8(Path.of(absolute)), getResultInputStream(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }


    @EnabledOnOs(OS.WINDOWS)
    @Test
    public void accessFileRelative_csvw_0000_windows() throws Exception {
        String relative = "src\\test\\resources\\csvw\\0000_trim.csv";
        Assume.assumeTrue(SystemUtils.IS_OS_WINDOWS);
        Access access = new LocalFileAccess("",relative, "csvw", "utf-8");
        try {
            assertEquals(readWithUTF8(Path.of(relative)), getResultUTF8(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @EnabledOnOs(OS.WINDOWS)
    @Test
    public void accessFileRelative_csvw_1000_windows() throws Exception {
        String relative = "src\\test\\resources\\csvw\\1000_nulls.csv";
        Assume.assumeTrue(SystemUtils.IS_OS_WINDOWS);
        Access access = new LocalFileAccess("",relative, "csvw", "utf-8");
        try {
            assertEquals(readWithUTF8(Path.of(relative)), getResultUTF8(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }
}
