package be.ugent.idlab.knows.dataio.access;

import be.ugent.idlab.knows.dataio.compression.Compression;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.input.BOMInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tukaani.xz.XZInputStream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serial;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

import static be.ugent.idlab.knows.dataio.utils.Utils.getHashOfString;

/**
 * This class represents access to a local file.
 */
public class LocalFileAccess implements Access {

    private static final Logger logger = LoggerFactory.getLogger(LocalFileAccess.class);
    @Serial
    private static final long serialVersionUID = -4721057992645925809L;
    private final String path;
    private String type;
    private final String encoding;
    private final Compression compression;

    /**
     * This constructor takes the path and the base path of a file.
     * When using the relative path for the file, put it in base and leave path empty
     *
     * @param path     the relative path of the file.
     * @param base     base for the path. If path is not absolute, path is used relative to base to find the file
     * @param type     type of the file
     * @param encoding encoding of the file
     * @param compression the compression type of the file
     */
    public LocalFileAccess(String path, String base, String type, Charset encoding, Compression compression) {
        if (base != null && !base.isEmpty()) {
            Path basePath = Path.of(base);
            this.path = basePath.resolve(path).toString();
        } else {
            this.path = Path.of(path).toString();
        }

        this.encoding = encoding.name();
        this.type = type;
        this.compression = compression;
    }

    /**
     * This constructor takes the path and the base path of a file.
     * When using the relative path for the file, put it in base and leave path empty.
     * The file is supposed to be uncompressed.
     *
     * @param path     the relative path of the file.
     * @param base     base for the path. If path is not absolute, path is used relative to base to find the file
     * @param type     type of the file
     * @param encoding encoding of the file
     */
    public LocalFileAccess(String path, String base, String type, Charset encoding) {
        this(path, base, type, encoding, Compression.None);
    }

    /**
     * Constructor with default charset UTF-8
     * @param path      the relative path of the file
     * @param basePath  base for the path. If path is not absolute, path is used relative to base to find the file
     * @param type      type of the file
     */
    public LocalFileAccess(String path, String basePath, String type) {
        this(path, basePath, type, StandardCharsets.UTF_8, Compression.None);
    }

    /**
     * Constructor in which filetype doesn't need to be specified.
     * Filetype is derived from the extension.
     * @param path      the relative path of the file
     * @param basePath  base for the path. If path is not absolute, then path is used relative to basePath to find the file
     */
    public LocalFileAccess(String path, String basePath) {
        this(path, basePath, null);
    }

    /**
     * This method returns the InputStream of the local file.
     *
     * @return an InputStream.
     * @throws FileNotFoundException when the file cannot be found.
     */
    @Override
    public InputStream getInputStream() throws IOException {
        Path path = Path.of(this.path);

        InputStream in = Files.newInputStream(path, StandardOpenOption.READ);

        InputStream inputStream = switch (this.compression) {
            case None -> in;
            case GZip ->  getGZInputStream(in);
            case Zip -> getZipInputStream(in);
            case XZ -> getXZInputStream(in);
            case Tar -> getTarInputStream(in);
            case TarXZ -> getTarXZInputStream(in);
            case TarGZ -> getTarGZInputStream(in);
        };

        return BOMInputStream.builder()
                .setInputStream(inputStream)
                .get();
    }

    private InputStream getTarGZInputStream(InputStream baseInputStream) throws IOException {
        return getTarInputStream(getGZInputStream(baseInputStream));
    }

    private InputStream getTarXZInputStream(InputStream baseInputStream) throws IOException {
        return getTarInputStream(getXZInputStream(baseInputStream));
    }

    private InputStream getZipInputStream(InputStream baseInputStream) throws IOException {
        ZipInputStream zip = new ZipInputStream(baseInputStream);
        // assumption: file to read is the first file in the stream
        zip.getNextEntry();
        return zip;
    }

    private InputStream getXZInputStream(InputStream baseInputStream) throws IOException {
        return new XZInputStream(baseInputStream);
    }

    private InputStream getGZInputStream(InputStream baseInputStream) throws IOException {
        return new GZIPInputStream(baseInputStream);
    }

    private InputStream getTarInputStream(InputStream baseInputStream) throws IOException {
        TarArchiveInputStream tar = new TarArchiveInputStream(baseInputStream);
        // assumption: file to read is the first file in the stream
        tar.getNextEntry();
        return tar;
    }

    /**
     * This method returns the datatypes of the file.
     * This method always returns null, because the datatypes can't be determined from a local file for the moment.
     *
     * @return the datatypes of the file.
     */
    @Override
    public Map<String, String> getDataTypes() {
        return Collections.singletonMap(getAccessPath(), this.type);
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof LocalFileAccess access) {
            boolean sameType;
            if (this.type == null) {
                sameType = access.type == null;
            } else {
                sameType = this.type.equals(access.type);
            }

            return path.equals(access.path) && sameType && encoding.equals(access.encoding);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getHashOfString(getAccessPath());
    }

    /**
     * This method returns the path of the access.
     *
     * @return the relative path.
     */
    public Path getPath() {
        return Path.of(path);
    }

    @Override
    public String toString() {
        return getAccessPath();
    }

    /**
     * Returns the content type reported by the underlying resource
     *
     * @return the content type as String
     */
    @Override
    public String getContentType() {
        if (this.type == null) {
            try {
                type = Files.probeContentType(Path.of(path));
            } catch (IOException e) {
                logger.warn("Could not get content type for path {}", path, e);
            }
        }

        return this.type;
    }

    /**
     * Path to the resource the Access represents, be it the URL, remote address, filepath...
     */
    @Override
    public String getAccessPath() {
        return Path.of(path).toAbsolutePath().toString();
    }

    public Charset getEncoding() {
        return Charset.forName(encoding);
    }
}
