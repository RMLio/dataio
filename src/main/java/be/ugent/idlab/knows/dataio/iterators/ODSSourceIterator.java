package be.ugent.idlab.knows.dataio.iterators;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.ods.ODSFileParser;
import be.ugent.idlab.knows.dataio.source.ODSSource;
import be.ugent.idlab.knows.dataio.source.Source;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

public class ODSSourceIterator extends SourceIterator {
    private String[] header;
    private String[] data;
    private Map<String, String> dataTypes;
    private ODSFileParser parser;


    /**
     * This function partly loads the full file in at once (to read the tables).
     * Opens the files using the access object and initiates the tableIterator, iterator and header.
     *
     * @param access the corresponding access object
     */
    public void open(Access access) {
        this.dataTypes = access.getDataTypes();

        try {
            this.parser = ODSFileParser.newInstance(access.getInputStream());
        } catch (XMLStreamException | IOException | SQLException e) {
            throw new RuntimeException(e);
        }

        this.header = this.parser.getHeader();

        // read initial data
        readData();
    }

    private void readData() {
        if (this.parser.canReadNextRow()) {
            this.data = this.parser.readNextRow();

            if (this.data.length != this.header.length) {
                throw new RuntimeException(String.format("The row read does not match the header.\nHeader: %s\nRow: %s", Arrays.toString(this.header), Arrays.toString(this.data)));
            }

        } else {
            this.data = null;
        }
    }

    @Override
    public boolean hasNext() {
        return this.data != null;
    }

    @Override
    public Source next() {
        String[] temp = this.data;
        readData();
        return new ODSSource(this.header, temp, this.dataTypes);
    }
}