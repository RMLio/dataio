package be.ugent.idlab.knows.dataio.compression;

import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarOutputStream;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Compressor {

    private final Compression compression;
    private final String compressedFileName;

    /**
     * Creates a new compressor.
     * @param compression           The type of compression.
     * @param compressedFileName    The name of the file entry to write in a zip or tar archive.
     */
    public Compressor(Compression compression, String compressedFileName) {
        this.compression = compression;
        this.compressedFileName = compressedFileName;
    }

    /**
     * Creates a new compressor.
     * @param compression           The type of compression.
     */
    public Compressor(Compression compression) {
        this(compression, null);
    }

    /**
     * Wraps the given OutputStream in a new compressed OutputStream, using the compression method set
     * in the constructor.
     * @param outputStream  The outputstream to compress.
     * @return              The wrapped outputstream, with compression.
     * @throws IOException
     */
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
        if (compressedFileName == null) {
            throw new IOException("No name given for the archive entry. Please construct a Compressor with a non-null compressedFileName parameter.");
        }
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        zip.putNextEntry(new ZipEntry(this.compressedFileName));
        return zip;
    }

    private OutputStream tar(OutputStream outputStream) throws IOException {
        if (compressedFileName == null) {
            throw new IOException("No name given for the archive entry. Please construct a Compressor with a non-null compressedFileName parameter.");
        }
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
