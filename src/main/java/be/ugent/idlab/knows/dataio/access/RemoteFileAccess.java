package be.ugent.idlab.knows.dataio.access;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

import static be.ugent.idlab.knows.dataio.utils.Utils.getHashOfString;
import static be.ugent.idlab.knows.dataio.utils.Utils.getInputStreamFromURL;

/**
 * This class represents access to a remote file.
 */
public class RemoteFileAccess implements Access {

    private String location;
    private String contentType;

    public RemoteFileAccess(String location) {
        this(location, "");
    }

    /**
     * This constructor of RemoteFileAccess taking location and content type as arguments.
     *
     * @param location    the location of the remote file.
     * @param contentType the content type of the remote file.
     */
    public RemoteFileAccess(String location, String contentType) {
        this.location = location;
        this.contentType = contentType;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return getInputStreamFromURL(new URL(location), contentType);
    }

    @Override
    public InputStreamReader getInputStreamReader() {
        return null;
    }

    /**
     * This method returns the datatypes of the file.
     * This method always returns null, because the datatypes can't be determined from a remote file for the moment.
     *
     * @return the datatypes of the file.
     */
    @Override
    public Map<String, String> getDataTypes() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RemoteFileAccess) {
            RemoteFileAccess access = (RemoteFileAccess) o;
            return location.equals(access.getLocation()) && contentType.equals(access.getContentType());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getHashOfString(getLocation() + getContentType());
    }

    /**
     * The method returns the location of the remote file.
     *
     * @return the location.
     */
    public String getLocation() {
        return location;
    }

    /**
     * This method returns the content type of the remote file.
     *
     * @return the content type.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Path to the resource the Access represents, be it the URL, remote address, filepath...
     */
    @Override
    public String getAccessPath() {
        return this.location;
    }
}
