package be.ugent.idlab.knows.access.localaccess;

import be.ugent.idlab.knows.access.Access;
import be.ugent.idlab.knows.access.LocalFileAccess;
import be.ugent.idlab.knows.access.cores.LocalAccessTestCore;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assume;
import org.junit.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ODSLocalAccessTest extends LocalAccessTestCore {

    // encoding issue
    @Test
    public void accessFileRelative_ods_0000_mightBeUTF8() throws Exception {
        String relative = "src/test/resources/ods/0000.ods";

        Access access = new LocalFileAccess("", relative, "ods", "ods");
        try {
            assertTrue(mightBeUTF8(access.getInputStream()));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileRelative_ods_0001_mightBeUTF8() throws Exception {
        String relative = "src/test/resources/ods/0001.ods";

        Access access = new LocalFileAccess("", relative, "ods", "ods");
        try {
            assertTrue(mightBeUTF8(access.getInputStream()));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileRelative_ods_0000_isUTF8() throws Exception {
        String relative = "src/test/resources/ods/0000.ods";

        Access access = new LocalFileAccess("", relative, "ods", "ods");
        try {
            assertTrue(isUTF8(access.getInputStream()));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileRelative_ods_0001_isUTF8() throws Exception {
        String relative = "src/test/resources/ods/0001.ods";

        Access access = new LocalFileAccess("", relative, "ods", "ods");
        try {
            assertTrue(isUTF8(access.getInputStream()));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileRelative_ods_0000_utf8() throws Exception {
        String relative = "src/test/resources/ods/0000.ods";

        Access access = new LocalFileAccess("", relative, "ods", "ods");
        try {
            assertEquals(readODS(new File(relative)), getResultUTF8(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileRelative_ods_0001_utf8() throws Exception {
        String relative = "src/test/resources/ods/0001.ods";

        Access access = new LocalFileAccess("", relative, "ods", "ods");
        try {
            assertEquals(readODS(new File(relative)), getResultUTF8(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileAbsolute_ods_0000_utf8() throws Exception {
        String relative = "src/test/resources/ods/0000.ods";
        String absolute = new File(relative).getAbsolutePath();
        readODS(new File(absolute));
        Access access = new LocalFileAccess(absolute, "", "ods", "ods");
        try {
            assertEquals(readODS(new File(absolute)), getResultUTF8(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileAbsolute_ods_0001_utf8() throws Exception {
        String relative = "src/test/resources/ods/0001.ods";
        String absolute = new File(relative).getAbsolutePath();

        Access access = new LocalFileAccess(absolute, "", "ods", "ods");
        try {
            assertEquals(readODS(new File(absolute)), getResultUTF8(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileAbsolute_ods_0000() throws Exception {
        String relative = "src/test/resources/ods/0000.ods";
        String absolute = new File(relative).getAbsolutePath();

        Access access = new LocalFileAccess(absolute, "", "ods", "ods");
        try {
            assertEquals(readODS(new File(absolute)), getResultInputStream(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileAbsolute_ods_0001() throws Exception {
        String relative = "src/test/resources/ods/0001.ods";
        String absolute = new File(relative).getAbsolutePath();

        Access access = new LocalFileAccess(absolute, "", "ods", "ods");
        try {
            assertEquals(readODS(new File(absolute)), getResultInputStream(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @EnabledOnOs(OS.WINDOWS)
    @Test
    public void accessFileRelative_ods_0000_windows() throws Exception {
        String relative = "src\\test\\resources\\ods\\0000.ods";
        Assume.assumeTrue(SystemUtils.IS_OS_WINDOWS);
        Access access = new LocalFileAccess("", relative, "ods", "ods");
        try {
            assertEquals(readODS(new File(relative)), getResultUTF8(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @EnabledOnOs(OS.WINDOWS)
    @Test
    public void accessFileRelative_ods_0001_windows() throws Exception {
        String relative = "src\\test\\resources\\ods\\0001.ods";
        Assume.assumeTrue(SystemUtils.IS_OS_WINDOWS);
        Access access = new LocalFileAccess("", relative, "ods", "ods");
        try {
            assertEquals(readODS(new File(relative)), getResultUTF8(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

}
