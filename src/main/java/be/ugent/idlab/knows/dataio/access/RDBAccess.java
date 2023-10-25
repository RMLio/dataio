package be.ugent.idlab.knows.dataio.access;

import be.ugent.idlab.knows.dataio.utils.NAMESPACES;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class represents the access to a relational database.
 */
public class RDBAccess implements Access {

    // Datatype definitions
    private final static String DOUBLE = "http://www.w3.org/2001/XMLSchema#double";
    private final static String VARBINARY = "http://www.w3.org/2001/XMLSchema#hexBinary";
    private final static String DECIMAL = "http://www.w3.org/2001/XMLSchema#decimal";
    private final static String INTEGER = "http://www.w3.org/2001/XMLSchema#integer";
    private final static String BOOLEAN = "http://www.w3.org/2001/XMLSchema#boolean";
    private final static String DATE = "http://www.w3.org/2001/XMLSchema#date";
    private final static String TIME = "http://www.w3.org/2001/XMLSchema#time";
    private final static String DATETIME = "http://www.w3.org/2001/XMLSchema#dateTime";
    @Serial
    private static final long serialVersionUID = 786349656223609949L;
    private String dsn;
    private final DatabaseType databaseType;
    private final String username;
    private final String password;
    private final String query;
    private final String contentType;
    private final Map<String, String> datatypes = new HashMap<>();

    private final static Logger log = LoggerFactory.getLogger(RDBAccess.class);


    /**
     * This constructor takes as arguments the dsn, database, username, password, query, content type
     *
     * @param dsn          the data source name.
     * @param databaseType the database type.
     * @param username     the username of the user that executes the query.
     * @param password     the password of the above user.
     * @param query        the SQL query to use.
     * @param contentType  the content type of the results.
     */
    public RDBAccess(String dsn, DatabaseType databaseType, String username, String password, String query, String contentType) {
        this.dsn = dsn;
        this.databaseType = databaseType;
        this.username = username;
        this.password = password;
        this.query = query;
        this.contentType = contentType;
    }

    /**
     * Convert a sequence of bytes to a string representation using uppercase hex symbols
     *
     * @param bytes the bytes to convert
     * @return a string containing the hexadecimal representation of the byte array
     */
    private static String bytesToHexString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            // format: 0 flag for zero-padding, 2 character width, uppercase hexadecimal symbols
            builder.append(String.format("%02X", b));
        }
        return builder.toString();
    }

    /**
     * Normalize the string representation of a data value given by the RDB.
     *
     * @param data     the string representation retrieved from the RDB of the data to be normalized.
     * @param dataType the intended datatype of the data parameter.
     * @return Normalized string representation of the data parameter, given the datatype.
     */
    private static String normalizeData(String data, String dataType) {
        if (DOUBLE.equals(dataType)) {
            // remove trailing decimal points (Quirk from MySQL, see issue 203)
            return data.replace(".0", "");
        }
        return data;
    }

    /**
     * This method returns an InputStream of the results of the SQL query.
     *
     * @return an InputStream with the results.
     */
    @Override
    public InputStream getInputStream() throws IOException, SQLException, ParserConfigurationException, TransformerException {
        InputStream inputStream;

        if (databaseType == DatabaseType.MYSQL) {
            StringBuilder parametersSB = new StringBuilder();
            boolean alreadySomeQueryParametersPresent = dsn.contains("?");
            if (alreadySomeQueryParametersPresent) {
                parametersSB.append("&");
            } else {
                parametersSB.append("?");
            }
            parametersSB.append("serverTimezone=UTC&useSSL=false"); // is this always true?
            dsn += parametersSB;
        }

        if (databaseType == DatabaseType.SQL_SERVER) {
            dsn = dsn.replaceAll("[?&]", ";");
        }

        try (
                // Open connection
                Connection connection = DriverManager.getConnection(dsn, username, password);
                Statement statement = connection.createStatement()
        ) {
            try (ResultSet rs = statement.executeQuery(query)) {
                if ((NAMESPACES.QL + "XPath").equals(contentType)) {
                    inputStream = getXMLInputStream(rs);
                } else {
                    inputStream = getCSVInputStream(rs);
                }
            }
        }

        return inputStream;
    }

    /**
     * This method returns the datatypes used for the columns in the accessed database.
     *
     * @return a map of column names and their datatypes.
     */
    @Override
    public Map<String, String> getDataTypes() {
        return datatypes;
    }

    /**
     * This method creates an CSV-formatted InputStream from a Result Set.
     *
     * @param rs the Result Set that is used.
     * @return a CSV-formatted InputStream.
     */
    private InputStream getCSVInputStream(ResultSet rs) throws SQLException {
        // Get number of requested columns
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();

        boolean filledInDataTypes = false;
        StringWriter writer = new StringWriter();

        // Differentiate null and ""
        CSVWriter csvWriter = new CSVWriter(writer);
        csvWriter.writeNext(getCSVHeader(rsmd, columnCount));

        // Extract data from result set
        while (rs.next()) {
            String[] csvRow = new String[columnCount];

            // Iterate over column names
            for (int i = 1; i <= columnCount; i++) {
                String columnName = rsmd.getColumnLabel(i);
                String dataType = getColumnDataType(rsmd.getColumnTypeName(i));

                // Register datatype during first encounter
                if (!filledInDataTypes) {
                    if (dataType != null) {
                        datatypes.put(columnName, dataType);
                    }
                }

                // Normalize value and add value to CSV row.
                if (VARBINARY.equals(dataType)) {
                    byte[] data = rs.getBytes(columnName);
                    csvRow[i - 1] = bytesToHexString(data);
                } else {
                    String data = rs.getString(columnName);
                    csvRow[i - 1] = normalizeData(data, dataType);
                }
            }

            // Add CSV row to CSVPrinter.
            // non-varargs call
            csvWriter.writeNext(csvRow);
            filledInDataTypes = true;
        }
        try {
            csvWriter.close();
        } catch (IOException e) {
            log.warn("Could not close CSVWriter.", e);
        }

        // Get InputStream from StringWriter.
        return new ByteArrayInputStream(writer.toString().getBytes());
    }

    private InputStream getXMLInputStream(ResultSet rs) throws SQLException, TransformerException, ParserConfigurationException {
        // Get number of requested columns
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();

        StringWriter writer = new StringWriter();

        // Create document
        DocumentBuilder builder;
        builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();

        Element rootElement = doc.createElement("Results");
        doc.appendChild(rootElement);
        // Extract data from result set
        while (rs.next()) {
            Element row = doc.createElement("row");
            rootElement.appendChild(row);

            // Iterate over column names
            for (int i = 1; i <= columnCount; i++) {
                Element el = doc.createElement(rsmd.getColumnName(i));
                el.appendChild(doc.createTextNode(rs.getObject(i).toString()));
                row.appendChild(el);
            }
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");

        transformer.transform(new DOMSource(doc), new StreamResult(writer));

        // Get InputStream from StringWriter.
        return new ByteArrayInputStream(writer.toString().getBytes());
    }

    /**
     * This method returns the corresponding datatype for a SQL datatype.
     *
     * @param type the SQL datatype.
     * @return the url of the corresponding datatype.
     */
    private String getColumnDataType(String type) {
        return switch (type.toUpperCase()) {
            case "BYTEA", "BINARY", "BINARY VARYING", "BINARY LARGE OBJECT", "VARBINARY" -> VARBINARY;
            case "NUMERIC", "DECIMAL" -> DECIMAL;
            case "SMALLINT", "INT", "INT4", "INT8", "INTEGER", "BIGINT" -> INTEGER;
            case "FLOAT", "FLOAT4", "FLOAT8", "REAL", "DOUBLE", "DOUBLE PRECISION" -> DOUBLE;
            case "BIT", "BOOL", "BOOLEAN" -> BOOLEAN;
            case "DATE" -> DATE;
            case "TIME" -> TIME;
            case "TIMESTAMP", "DATETIME" -> DATETIME;
            default -> null;
        };
    }

    /**
     * This method returns the header of the CSV.
     *
     * @param rsmd        metdata of the Result Set
     * @param columnCount the number of columns.
     * @return a String array with the headers.
     */
    private String[] getCSVHeader(final ResultSetMetaData rsmd, final int columnCount) throws SQLException {
        String[] headers = new String[columnCount];

        for (int i = 1; i <= columnCount; i++) {
            headers[i - 1] = rsmd.getColumnLabel(i);
            // Setting the empty header label at be.ugent.rml.access.RDBAccess.nullheader (as otherwise CSV parsers might fail),
            // (this header cannot be used by actual mapping files so this should actually not give any issues)
            //  and hope that this header will NEVER be encountered in real-world tables
            if (headers[i - 1] == null || headers[i - 1].isEmpty()) {
                headers[i - 1] = "be.ugent.rml.access.RDBAccess.nullheader";
            }
        }

        return headers;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RDBAccess access) {
            return dsn.equals(access.getDSN())
                    && databaseType.equals(access.getDatabaseType())
                    && username.equals(access.getUsername())
                    && password.equals(access.getPassword())
                    && query.equals(access.getQuery())
                    && contentType.equals(access.getContentType());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getDSN() + getDatabaseType() + getUsername() + getPassword() + getQuery() + getContentType());
    }

    /**
     * This method returns the DNS.
     *
     * @return the DNS.
     */
    public String getDSN() {
        return dsn;
    }

    /**
     * This method returns the database type.
     *
     * @return the database type.
     */
    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    /**
     * This method returns the username.
     *
     * @return the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * This method returns the password.
     *
     * @return the password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * This method returns the SQL query.
     *
     * @return the SQL query.
     */
    public String getQuery() {
        return query;
    }

    /**
     * This method returns the content type.
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
        return getDSN();
    }
}
