package be.ugent.idlab.knows.dataio.access.localaccess;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.access.cores.LocalAccessTestCore;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assume;
import org.junit.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XMLLocalAccessTest extends LocalAccessTestCore {


    @Test
    public void accessFileRelative_xml_0000_mightBeUTF8() throws Exception {
        String relative = "src/test/resources/xml/0000.xml";

        Access access = new LocalFileAccess("", relative, "xml", "utf-8");
        try {
            assertTrue(mightBeUTF8(access.getInputStream()));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileRelative_xml_0001_mightBeUTF8() throws Exception {
        String relative = "src/test/resources/xml/0001.xml";

        Access access = new LocalFileAccess("", relative, "xml", "utf-8");
        try {
            assertTrue(mightBeUTF8(access.getInputStream()));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileRelative_xml_0000_isUTF8() throws Exception {
        String relative = "src/test/resources/xml/0000.xml";

        Access access = new LocalFileAccess("", relative, "xml", "utf-8");
        try {
            assertTrue(isUTF8(access.getInputStream()));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileRelative_xml_0001_isUTF8() throws Exception {
        String relative = "src/test/resources/xml/0001.xml";

        Access access = new LocalFileAccess("", relative, "xml", "utf-8");
        try {
            assertTrue(isUTF8(access.getInputStream()));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileRelative_xml_0001_utf8() throws Exception {
        String relative = "src/test/resources/xml/0001.xml";

        Access access = new LocalFileAccess("", relative, "xml", "utf-8");
        try {
            assertEquals(readWithUTF8(Path.of(relative)), getResultUTF8(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileAbsolute_xml_0000_utf8() throws Exception {
        String relative = "src/test/resources/xml/0000.xml";
        String absolute = new File(relative).getAbsolutePath();

        Access access = new LocalFileAccess(absolute, "", "xml", "utf-8");
        try {
            assertEquals(readWithUTF8(Path.of(absolute)), getResultUTF8(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileAbsolute_xml_0001_utf8() throws Exception {
        String relative = "src/test/resources/xml/0001.xml";
        String absolute = new File(relative).getAbsolutePath();

        Access access = new LocalFileAccess(absolute, "", "xml", "utf-8");
        try {
            assertEquals(readWithUTF8(Path.of(absolute)), getResultUTF8(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileAbsolute_xml_0000() throws Exception {
        String relative = "src/test/resources/xml/0000.xml";
        String absolute = new File(relative).getAbsolutePath();

        Access access = new LocalFileAccess(absolute, "", "xml", "utf-8");
        try {
            assertEquals(readWithUTF8(Path.of(absolute)), getResultInputStream(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileAbsolute_xml_0001() throws Exception {
        String relative = "src/test/resources/xml/0001.xml";
        String absolute = new File(relative).getAbsolutePath();

        Access access = new LocalFileAccess(absolute, "", "xml", "utf-8");
        try {
            assertEquals(readWithUTF8(Path.of(absolute)), getResultInputStream(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    public void accessFileAbsolute_xml_BOM_0000() throws Exception {
        String relative = "src/test/resources/xml/0000_BOM.xml";
        String absolute = new File(relative).getAbsolutePath();

        Access access = new LocalFileAccess(absolute, "", "xml", "utf-8");
        try {
            assertEquals(readWithUTF8(Path.of(absolute)), getResultInputStream(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @EnabledOnOs(OS.WINDOWS)
    @Test
    public void accessFileRelative_xml_0001_windows() throws Exception {
        String relative = "src\\test\\resources\\xml\\0001.xml";
        Assume.assumeTrue(SystemUtils.IS_OS_WINDOWS);
        Access access = new LocalFileAccess("", relative, "xml", "utf-8");
        try {
            assertEquals(readWithUTF8(Path.of(relative)), getResultUTF8(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @EnabledOnOs(OS.WINDOWS)
    @Test
    public void accessFileAbsolute_xml_0000_windows() throws Exception {
        String relative = "src\\test\\resources\\xml\\0000.xml";
        String absolute = new File(relative).getAbsolutePath();
        Assume.assumeTrue(SystemUtils.IS_OS_WINDOWS);
        Access access = new LocalFileAccess(absolute, "", "xml", "utf-8");
        try {
            assertEquals(readWithUTF8(Path.of(absolute)), getResultUTF8(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }
}
