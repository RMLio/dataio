package be.ugent.idlab.knows.dataio.export;

import be.ugent.idlab.knows.dataio.compression.Compression;
import be.ugent.idlab.knows.dataio.compression.Compressor;

import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * A class to handle writing to file.
 */
public class FileExport implements Export {

    private final String filePath;
    private final Compression compression;
    private final String compressedFileName;


    /**
     * Creates an instance with a given file path without applying compression.
     * @param filePath              The path to the output file.
     */
    public FileExport(String filePath) {
        this(filePath, Compression.None);
    }

    /**
     * Creates an instance with a given file path and compression configuration.
     * @param filePath              The path to the output file.
     * @param compression           The type of compression to apply.
     */
    public FileExport(String filePath, Compression compression) {
        this(filePath, compression, null);
    }

    /**
     * Creates an instance with a given file path and compression configuration.
     * @param filePath              The path to the output file.
     * @param compression           The type of compression to apply.
     * @param compressedFileName    The name of the file entry in an archive.
     */
    public FileExport(String filePath, Compression compression, String compressedFileName) {
        this.filePath = filePath;
        this.compression = compression;
        this.compressedFileName = compressedFileName;
    }

    @Override
    public OutputStream getOutputStream() throws Exception {
        OutputStream out = new FileOutputStream(filePath);

        if (compression != Compression.None) {
            return new Compressor(this.compression, this.compressedFileName).compress(out);
        }

        return out;
    }
}
