package be.ugent.idlab.knows.dataio.compression;

import jdk.jshell.spi.ExecutionControl;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarOutputStream;
import org.tukaani.xz.FilterOptions;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZOutputStream;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Compressor {

    private final Compression compression;
    private String compressedFileName = "out.txt";

    public Compressor(Compression compression, String compressedFileName) {
        this.compression = compression;
        if (compressedFileName != null) {
            this.compressedFileName = compressedFileName;
        }
    }

    public Compressor(Compression compression) {
        this.compression = compression;
    }

    public OutputStream compress(OutputStream outputStream) throws IOException {
        return switch (this.compression) {
            case None -> outputStream;
            case GZip -> gzip(outputStream);
            case Zip -> zip(outputStream);
            case XZ -> xz(outputStream);
            case Tar -> tar(outputStream);
            case TarXZ -> xz(tar(outputStream));
            case TarGZ -> gzip(tar(outputStream));
        };
    }

    private OutputStream zip(OutputStream outputStream) throws IOException {
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        zip.putNextEntry(new ZipEntry(this.compressedFileName));
        return zip;
    }

    private OutputStream tar(OutputStream outputStream) throws IOException {
        TarOutputStream tar = new TarOutputStream(outputStream);
        TarEntry entry = new TarEntry(this.compressedFileName);
        tar.putNextEntry(entry);
        return tar;
    }

    private OutputStream gzip(OutputStream outputStream) throws IOException {
        return new GZIPOutputStream(outputStream);
    }

    private OutputStream xz(OutputStream outputStream) throws IOException {
        return new XZOutputStream(outputStream, new LZMA2Options());
    }
}
