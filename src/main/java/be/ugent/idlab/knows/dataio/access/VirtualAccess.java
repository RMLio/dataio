package be.ugent.idlab.knows.dataio.access;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Map;

/**
 * A class made to mimic other Access objects.
 * Can be used as a way to cache fields from other Accesses that are built on the fly, such as datatypes
 */
public class VirtualAccess implements Access {

    private final byte[] data;
    private final Map<String, String> dataTypes;
    private final String contentType;
    private final String accessPath;

    public VirtualAccess(Access other) throws IOException, SQLException {
        try(InputStream is = other.getInputStream()) {
            this.data = is.readAllBytes();
        }

        this.dataTypes = other.getDataTypes();
        this.contentType = other.getContentType();
        this.accessPath = other.getAccessPath();
    }

    @Override
    public InputStream getInputStream() throws IOException, SQLException {
        return new ByteArrayInputStream(this.data);
    }

    @Override
    public InputStreamReader getInputStreamReader() throws IOException, SQLException {
        return new InputStreamReader(this.getInputStream());
    }

    @Override
    public Map<String, String> getDataTypes() {
        return this.dataTypes;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public String getAccessPath() {
        return this.accessPath;
    }
}
