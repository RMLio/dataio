package be.ugent.idlab.knows.dataio.iterators.ods;

import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Parser for reading ODS files. Default implementation relies on StAX to parse the file.
 */
public interface ODSFileParser {
    /**
     * Provides default implementation of the ODSFileParser
     *
     * @param path path to the ODS file
     * @return a fresh instance of the default parser
     * @throws XMLStreamException when parser was unable to position itself within the file
     * @throws IOException        when the passed file could not have been read
     */
    static ODSFileParser newInstance(String path) throws XMLStreamException, IOException {
        return ODSFileParser.newInstance(new FileInputStream(path));
    }

    /**
     * Provides default implementation of the ODSFileParser.
     *
     * @param inputStream file stream of the ODS file
     * @return a fresh instance of the default parser
     * @throws XMLStreamException when parser was unable to position itself within the file
     * @throws IOException        when unable to read the filestream
     */
    static ODSFileParser newInstance(InputStream inputStream) throws XMLStreamException, IOException {
        // ODS file is nothing more than a glorified ZIP file
        try (ZipInputStream zip = new ZipInputStream(inputStream)) {
            // linear search for the file within the zip stream with values
            ZipEntry e = zip.getNextEntry();

            if (e == null) {
                throw new IllegalArgumentException("The passed file is empty or not a valid ODS file!");
            }

            while (!(e == null || e.getName().equals("content.xml"))) {
                e = zip.getNextEntry();
            }

            if (e == null) {
                throw new IllegalArgumentException("File is not a valid ODS file: does not contain contents.xml!");
            }

            return new StdODSFileParser(zip);
        }
    }


    /**
     * Gets header for the current table
     * Header is assumed to be the first row in the file
     *
     * @return a string array of header values
     */
    String[] getHeader();

    /**
     * Reads the next row of the file, parsing it into string values.
     * The number of cells read should be equal to the length of the header, throwing out the missing values
     *
     * @return a string array of the values of the cells in the row, cast into strings
     */
    String[] readNextRow();

    /**
     * Moves the parser to the next row
     */
    void moveToNextRow();

    /**
     * Moves the parser to the next table, if present
     *
     * @return true if the parser was moved to the next table, false if no next table present
     */
    boolean moveToNextTable();

    /**
     * Forwards the parser to the next row
     *
     * @return true if the parser is positioned at the next row, false if the parser could not find a next row within the table
     */
    boolean canReadNextRow();
}

