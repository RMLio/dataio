package be.ugent.idlab.knows.dataio.export;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.compression.Compression;
import org.apache.tools.tar.TarOutputStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.OutputStream;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExportTest {

    @Nested
    class CompressionTest {

        private final String file = "/tmp/foo";
        private final String contents = "foo,bar,baz";


        @AfterEach
        public void cleanup() {
            File f = new File("/tmp/foo");
            f.delete();
        }

        @Test
        public void testNone() throws Exception {
            FileExport export = new FileExport(file);
            OutputStream out = export.getOutputStream();
            out.write(contents.getBytes());
            out.close();

            Access access = new LocalFileAccess(file, "", "txt", Charset.defaultCharset());
            String actual = new String(access.getInputStream().readAllBytes());

            assertEquals(contents, actual);
        }

        @Test
        public void testGzip() throws Exception {
            FileExport export = new FileExport(file, Compression.GZip);
            OutputStream out = export.getOutputStream();
            out.write(contents.getBytes());
            out.close();

            Access access = new LocalFileAccess(file, "", "gzip", Charset.defaultCharset(), Compression.GZip);
            String actual = new String(access.getInputStream().readAllBytes());

            assertEquals(contents, actual);
        }

        @Test
        public void testZip() throws Exception {
            FileExport export = new FileExport(file, Compression.Zip);
            OutputStream out = export.getOutputStream();
            out.write(contents.getBytes());
            out.close();

            Access access = new LocalFileAccess(file, "", "zip", Charset.defaultCharset(), Compression.Zip);
            String actual = new String(access.getInputStream().readAllBytes());

            assertEquals(contents, actual);
        }

        @Test
        public void testXZ() throws Exception {
            FileExport export = new FileExport(file, Compression.XZ);
            OutputStream out = export.getOutputStream();
            out.write(contents.getBytes());
            out.close();

            Access access = new LocalFileAccess(file, "", "xz", Charset.defaultCharset(), Compression.XZ);
            String actual = new String(access.getInputStream().readAllBytes());

            assertEquals(contents, actual);
        }

        @Disabled("Needs proper implementation")
        @Test
        public void testTar() throws Exception {
            FileExport export = new FileExport(file, Compression.Tar);
            OutputStream out = export.getOutputStream();
            out.write(contents.getBytes());
            out.close();

            Access access = new LocalFileAccess(file, "", "tar", Charset.defaultCharset(), Compression.Tar);
            String actual = new String(access.getInputStream().readAllBytes());

            assertEquals(contents, actual);
        }

        @Disabled("Needs proper implementation")
        @Test
        public void testTarGZ() throws Exception {
            FileExport export = new FileExport(file, Compression.TarGZ);
            TarOutputStream out = (TarOutputStream) export.getOutputStream();
            out.write(contents.getBytes());
            out.closeEntry();
            out.close();

            Access access = new LocalFileAccess(file, "", "tar.gz", Charset.defaultCharset(), Compression.TarGZ);
            String actual = new String(access.getInputStream().readAllBytes());
            assertEquals(contents, actual);
        }

        @Disabled("Needs proper implementation")
        @Test
        public void testTarXZ() throws Exception {
            FileExport export = new FileExport(file, Compression.TarXZ);
            OutputStream out = export.getOutputStream();
            out.write(contents.getBytes());
            out.close();

            Access access = new LocalFileAccess(file, "", "tar.xz", Charset.defaultCharset(), Compression.TarXZ);
            String actual = new String(access.getInputStream().readAllBytes());
            assertEquals(contents, actual);
        }
    }
}
