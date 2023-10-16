package be.ugent.idlab.knows.dataio.access;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Map;

/**
 * This interface represents the access to a data source.
 * For example, a local file, a remote file, a relational database, and so on.
 */
public interface Access extends Serializable {

    /**
     * This method returns an InputStream for the access.
     *
     * @return the InputStream corresponding to the access.
     */
    InputStream getInputStream() throws Exception;

    InputStreamReader getInputStreamReader() throws Exception;

    /**
     * This method returns a map of datatypes.
     * References to values are mapped to their datatypes, if available.
     *
     * @return map of datatypes.
     */
    Map<String, String> getDataTypes();

    String getContentType();

    /**
     * Path to the resource the Access represents, be it the URL, remote address, filepath...
     */
    String getAccessPath();
}
