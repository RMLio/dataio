package be.ugent.idlab.knows.dataio.access;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * A class made to mimic other Access objects.
 * Can be used as a way to cache fields from other Accesses that are built on the fly, such as datatypes
 */
public class VirtualAccess implements Access {

    private static final long serialVersionUID = 3814081534660206173L;
    private final byte[] data;
    private Map<String, String> dataTypes;
    private String contentType;
    private String accessPath;

    public VirtualAccess(Access other) throws Exception {
        try (InputStream is = other.getInputStream()) {
            this.data = is.readAllBytes();
        }

        this.dataTypes = other.getDataTypes();
        this.contentType = other.getContentType();
        this.accessPath = other.getAccessPath();
    }

    public VirtualAccess(byte[] data) {
        this.data = data;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.data);
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
