package be.ugent.idlab.knows.dataio.access;

import org.apache.commons.io.input.BOMInputStream;
import org.apache.tika.parser.txt.CharsetDetector;
import org.apache.tika.parser.txt.CharsetMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static be.ugent.idlab.knows.dataio.utils.Utils.getHashOfString;
import static org.apache.commons.io.FileUtils.getFile;
import static org.apache.commons.io.FilenameUtils.getExtension;

/**
 * This class represents access to a local file.
 */
public class LocalFileAccess implements Access {

    private static final Logger logger = LoggerFactory.getLogger(LocalFileAccess.class);
    private static final int CONFIDENCE_LIMIT = 90;
    private final String path;
    private final String base;
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
        this.path = path;
        this.base = base;

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
     * @throws FileNotFoundException
     */
    @Override
    public InputStream getInputStream() throws IOException {
        File file = new File(this.path);

        if (!file.isAbsolute()) {
            file = getFile(this.base, this.path);
        }

        encodingCheck(file);

        return new BOMInputStream(new FileInputStream(file), false);
    }

    private void encodingCheck(File file) throws FileNotFoundException {
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        List<CharsetMatch> matches;
        try {
            matches = Arrays.stream(new CharsetDetector().setText(in).detectAll())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                in.close();
            } catch (IOException ignored) {
            }
        }

        List<String> matchesNames = matches.stream().map(CharsetMatch::getName).collect(Collectors.toList());

        if (!matchesNames.contains(this.encoding)) {

            // only warn if high confidence
            if (matches.get(0).getConfidence() > CONFIDENCE_LIMIT) {
                // matches are sorted based on confidence
                String message = String.format("Detected encoding doesn't match the passed encoding! Most likely encoding of %s is %s, got passed %s", file.getName(), matches.get(0).getName(), this.encoding);
                logger.warn(message);
            }
        }
    }

    @Override
    public InputStreamReader getInputStreamReader() throws FileNotFoundException {
        return new FileReader(new File(this.base, this.path));
    }


    /**
     * This method returns the datatypes of the file.
     * This method always returns null, because the datatypes can't be determined from a local file for the moment.
     *
     * @return the datatypes of the file.
     */
    @Override
    public Map<String, String> getDataTypes() {
        return Collections.singletonMap(getFullPath(), this.type);
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof LocalFileAccess) {
            LocalFileAccess access = (LocalFileAccess) o;
            return path.equals(access.path) && base.equals(access.getBase()) && type.equals(access.type) && encoding.equals(access.encoding);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getHashOfString(getFullPath());
    }

    /**
     * This method returns the path of the access.
     *
     * @return the relative path.
     */
    public Path getPath() {
        return Path.of(path);
    }

    /**
     * This method returns the base path of the access.
     *
     * @return the base path.
     */
    public String getBase() {
        return base;
    }

    @Override
    public String toString() {
        return getFullPath();
    }

    @Override
    public String getContentType() {
        return getExtension(this.path);
    }

    /**
     * Path to the resource the Access represents, be it the URL, remote address, filepath...
     */
    @Override
    public String getAccessPath() {
        File file = new File(this.path);

        if (!file.isAbsolute()) {
            file = getFile(this.base, this.path);
        }

        return file.getAbsolutePath();
    }

    private String getFullPath() {
        File file = new File(this.path);
        String fullPath = this.path;
        if (!file.isAbsolute()) {
            fullPath = this.base + this.path;
        }
        return fullPath;
    }

    public String getEncoding() {
        return encoding;
    }
}
