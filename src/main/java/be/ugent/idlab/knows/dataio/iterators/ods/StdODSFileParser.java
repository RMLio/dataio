package be.ugent.idlab.knows.dataio.iterators.ods;

import be.ugent.idlab.knows.dataio.exceptions.BadHeaderException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class StdODSFileParser implements ODSFileParser {
    private final StAXParser parser;
    private String[] header;

    public StdODSFileParser(InputStream stream) throws XMLStreamException {
        this.parser = new StAXParser(stream);
        this.header = this.readNextRow();

        // check for header to be correct (no null values)
        if (this.header == null) {
            throw new BadHeaderException("empty");
        } else if (Arrays.stream(this.header).anyMatch(Objects::isNull)) {
            throw new BadHeaderException(Arrays.toString(this.header));
        }
    }

    @Override
    public String[] getHeader() {
        return header;
    }

    /**
     * Checks if the parser is at a correct start position.
     *
     * @return true if the parser is at expected position, false otherwise
     */
    private boolean checkParserStartPosition(String prefix, String localPart) {
        if (parser.cursor == null || !parser.cursor.isStartElement()) {
            return false;
        }
        StartElement e = parser.cursor.asStartElement();
        QName name = e.getName();
        return name.getPrefix().equals(prefix) && name.getLocalPart().equals(localPart);
    }

    private boolean checkParserEndPosition(String prefix, String localPart) {
        if (!parser.cursor.isEndElement()) {
            return false;
        }

        EndElement e = parser.cursor.asEndElement();
        QName name = e.getName();
        return name.getPrefix().equals(prefix) && name.getLocalPart().equals(localPart);
    }

    public void readHeader() throws XMLStreamException {
        this.parser.forwardToStartElement("table", "table-row");

        this.header = readRow();
    }

    @Override
    public String[] readNextRow() {
        try {
            if (!this.checkParserStartPosition("table", "table-row")) {
                // attempt to move to the next row
                this.parser.forwardToStartElement("table", "table-row");

                if (!this.checkParserStartPosition("table", "table-row")) {
                    // if the next row still hasn't been reached, there is no more row to be read
                    return null;
                }

            }
            return readRow();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean canReadNextRow() {
        return checkParserStartPosition("table", "table-row");
    }

    @Override
    public boolean moveToNextTable() {
        this.parser.forwardToStartElement("table", "table");
        if (checkParserStartPosition("table", "table")) {
            try {
                readHeader();
            } catch (XMLStreamException e) {
                throw new RuntimeException(e);
            }
            return true;
        }

        return false;
    }

    public void moveToNextRow() {
        this.parser.forwardToStartElement("table", "table-row");
    }

    /**
     * Advance the parser to the next event
     */
    private void advance() {
        this.parser.advance();
    }

    /**
     * Reads a single row from the file.
     *
     * @return an array of values contained within the read row
     */
    private String[] readRow() throws XMLStreamException {
        // each row consists of cells
        List<String> values = new ArrayList<>();
        this.parser.forwardToStartElement("table", "table-cell");

        values.add(readCell());
        advance();
        while (checkParserStartPosition("table", "table-cell")) {
            if (this.parser.cursor.isStartElement() && this.parser.cursor.isEndElement()) {
                System.out.println("Self closing");
            }
            values.add(readCell());
            advance();
        }

        advance(); // close the row

        return values.toArray(new String[0]);
    }

    /**
     * Reads a cell from the row
     *
     * @return value of the cell as a String or null if the cell is empty
     */
    private String readCell() {
        // cursor is expected to point to the start of the cell element
        // advance to <text:p>
        advance();

        if (checkParserEndPosition("table", "table-cell")) { // empty cell
            return null;
        }
        // advance to char event
        advance();
        StringBuilder sb = new StringBuilder();
        sb.append(parser.cursor.asCharacters().getData());

        advance();

        while (this.parser.cursor.isCharacters()) { // stuff like &apos; causes another char event
            sb.append(parser.cursor.asCharacters().getData());
            advance();
        }

        // advance to </table:table-cell>
        advance();
        return sb.toString();
    }
}

