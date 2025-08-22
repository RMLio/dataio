package be.ugent.idlab.knows.dataio.export;

import be.ugent.idlab.knows.dataio.compression.Compression;
import be.ugent.idlab.knows.dataio.compression.Compressor;

import java.io.FileOutputStream;
import java.io.OutputStream;

public class FileExport implements Export {

    private final String filePath;
    private final Compression compression;
    private String compressedFileName = "out.txt";

    public FileExport(String filePath) {
        this(filePath, Compression.None);
    }

    public FileExport(String filePath, Compression compression) {
        this(filePath, compression, null);
    }

    public FileExport(String filePath, Compression compression, String compressedFileName) {
        this.filePath = filePath;
        this.compression = compression;

        if (compressedFileName != null) {
            this.compressedFileName = compressedFileName;
        }
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
