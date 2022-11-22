package be.ugent.idlab.knows.dataio.access.localaccess;

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
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExcelLocalAccessTest extends LocalAccessTestCore {

    // encoding issue
    @Test
    public void accessFileRelative_excel_0000_mightBeUTF8() throws Exception {
        String relative = "src/test/resources/excel/0000.xlsx";

        LocalFileAccess access = new LocalFileAccess("", relative, "xlsx", "xlsx");
        try {
            assertTrue(mightBeUTF8(access.getInputStream()));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileRelative_excel_0001_mightBeUTF8() throws Exception {
        String relative = "src/test/resources/excel/0001.xlsx";

        LocalFileAccess access = new LocalFileAccess("", relative, "xlsx", "xlsx");
        try {
            assertTrue(mightBeUTF8(access.getInputStream()));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileRelative_excel_0000_isUTF8() throws Exception {
        String relative = "src/test/resources/excel/0000.xlsx";

        LocalFileAccess access = new LocalFileAccess("", relative, "xlsx", "xlsx");
        try {
            assertTrue(isUTF8(access.getInputStream()));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileRelative_excel_0001_isUTF8() throws Exception {
        String relative = "src/test/resources/excel/0001.xlsx";

        LocalFileAccess access = new LocalFileAccess("", relative, "xlsx", "xlsx");
        try {
            assertTrue(isUTF8(access.getInputStream()));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileRelative_excel_0000_utf8() throws Exception {
        String relative = "src/test/resources/excel/0000.xlsx";

        LocalFileAccess access = new LocalFileAccess("", relative, "xlsx", "xlsx");
        try {
            assertEquals(readExcel(new File(relative)), getResultUTF8(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileRelative_excel_0001_utf8() throws Exception {
        String relative = "src/test/resources/excel/0001.xlsx";

        LocalFileAccess access = new LocalFileAccess("", relative, "xlsx", "xlsx");
        try {
            assertEquals(readExcel(new File(relative)), getResultUTF8(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void accessFileAbsolute_excel_0000_utf8() throws Exception {
        String relative = "src/test/resources/excel/0000.xlsx";
        String absolute = new File(relative).getAbsolutePath();

        LocalFileAccess access = new LocalFileAccess(absolute, "", "xlsx", "xlsx");
        try {
            assertEquals(readExcel(new File(absolute)), getResultUTF8(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileAbsolute_excel_0001_utf8() {
        String relative = "src/test/resources/excel/0001.xlsx";
        String absolute = new File(relative).getAbsolutePath();

        LocalFileAccess access = new LocalFileAccess(absolute, "", "xlsx", "xlsx");
        try {
            assertEquals(readExcel(new File(absolute)), getResultUTF8(access, StandardCharsets.UTF_8));
        } catch (SQLException | FileNotFoundException | ClassNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    public void accessFileAbsolute_excel_0000() throws Exception {
        String relative = "src/test/resources/excel/0000.xlsx";
        String absolute = new File(relative).getAbsolutePath();

        LocalFileAccess access = new LocalFileAccess(absolute, "", "xlsx", "xlsx");
        try {
            assertEquals(readExcel(new File(absolute)), getResultInputStream(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @Test
    public void accessFileAbsolute_excel_0001() throws Exception {
        String relative = "src/test/resources/excel/0001.xlsx";
        String absolute = new File(relative).getAbsolutePath();

        LocalFileAccess access = new LocalFileAccess(absolute, "", "xlsx", "xlsx");
        try {
            assertEquals(readExcel(new File(absolute)), getResultInputStream(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    @EnabledOnOs(OS.WINDOWS)
    @Test
    public void accessFileRelative_excel_0000_windows() throws Exception {
        String relative = "src\\test\\resources\\excel\\0000.xlsx";
        Assume.assumeTrue(SystemUtils.IS_OS_WINDOWS);
        LocalFileAccess access = new LocalFileAccess("", relative, "xlsx", "xlsx");
        try {
            assertEquals(readExcel(new File(relative)), getResultUTF8(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }


    @EnabledOnOs(OS.WINDOWS)
    @Test
    public void accessFileRelative_excel_0001_windows() throws Exception {
        String relative = "src\\test\\resources\\excel\\0001.xlsx";
        Assume.assumeTrue(SystemUtils.IS_OS_WINDOWS);
        LocalFileAccess access = new LocalFileAccess("", relative, "xlsx", "xlsx");
        try {
            assertEquals(readExcel(new File(relative)), getResultUTF8(access, StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }
}
