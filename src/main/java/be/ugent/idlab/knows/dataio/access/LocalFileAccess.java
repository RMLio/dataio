package be.ugent.idlab.knows.dataio.access;

import org.apache.commons.io.input.BOMInputStream;
import org.apache.tika.parser.txt.CharsetDetector;
import org.apache.tika.parser.txt.CharsetMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

import static be.ugent.idlab.knows.dataio.utils.Utils.getHashOfString;

/**
 * This class represents access to a local file.
 */
public class LocalFileAccess implements Access {

    private static final Logger logger = LoggerFactory.getLogger(LocalFileAccess.class);
    private static final int CONFIDENCE_LIMIT = 90;
    @Serial
    private static final long serialVersionUID = -4721057992645925809L;
    private final String path;
    private final String type;
    private final String encoding;

    /**
     * This constructor takes the path and the base path of a file.
     * When using the relative path for the file, put it in base and leave path empty
     *
     * @param path     the relative path of the file.
     * @param base     base for the path. If path is not absolute, path is used relative to base to find the file
     * @param type     type of the file
     * @param encoding encoding of the file
     */
    public LocalFileAccess(String path, String base, String type, String encoding) {
        if (base != null && !base.isEmpty()) {
            Path basePath = Path.of(base);
            this.path = basePath.resolve(path).toString();
        } else {
            this.path = Path.of(path).toString();
        }

        if (!Charset.isSupported(encoding)) {
            throw new IllegalArgumentException("Passed encoding not supported.");
        }
        this.encoding = encoding;
        this.type = type;
    }

    public LocalFileAccess(String path, String basePath, String type) {
        this(path, basePath, type, "utf-8");
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
        encodingCheck(path);
        return new BOMInputStream(Files.newInputStream(path, StandardOpenOption.READ));
    }

    private void encodingCheck(Path path) throws IOException {
        try (InputStream in = new BufferedInputStream(Files.newInputStream(path, StandardOpenOption.READ)) ) {
            List<CharsetMatch> matches = Arrays.stream(new CharsetDetector().setText(in).detectAll()).toList();
            Set<String> matchesNames = matches.stream().map(CharsetMatch::getName).collect(Collectors.toSet());
            if (!matchesNames.contains(this.encoding)) {
                // only warn if high confidence
                if (matches.get(0).getConfidence() > CONFIDENCE_LIMIT) {
                    // matches are sorted based on confidence
                    logger.warn("Detected encoding doesn't match the passed encoding! Most likely encoding of {} is {}, got passed {}", path, matches.get(0).getName(), this.encoding);
                }
            }
        }
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
            return path.equals(access.path) && type.equals(access.type) && encoding.equals(access.encoding);
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

    @Override
    public String getContentType() {
        MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();
        return fileTypeMap.getContentType(path);
    }

    /**
     * Path to the resource the Access represents, be it the URL, remote address, filepath...
     */
    @Override
    public String getAccessPath() {
        return Path.of(path).toAbsolutePath().toString();
    }

    public String getEncoding() {
        return encoding;
    }
}
