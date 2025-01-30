package be.ugent.idlab.knows.dataio.access;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Map;

/**
 * This interface represents the access to a data source.
 * For example, a local file, a remote file, a relational database, and so on.
 */
public interface Access extends Serializable {
    /**
     * This method returns an InputStream for the access. This method
     *
     * @return An InputStream from the respective source
     *
     * @throws IOException when something goes wrong with reading a file from disk
     * @throws SQLException when something goes wrong in communication with database
     * @throws ParserConfigurationException when something goes wrong with XML reading
     * @throws TransformerException when something goes wrong with XML reading
     */
    InputStream getInputStream() throws IOException, SQLException, ParserConfigurationException, TransformerException;

    /**
     * This method returns a map of datatypes.
     * References to values are mapped to their datatypes, if available.
     *
     * @return map of datatypes.
     */
    Map<String, String> getDataTypes();

    /**
     * Returns the content type reported by the underlying resource
     *
     * @return the content type as String
     */
    String getContentType();

    /**
     * Path to the resource the Access represents, be it the URL, remote address, filepath...
     *
     * @return a path to the resource
     */
    String getAccessPath();
}
