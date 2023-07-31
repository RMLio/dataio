package be.ugent.idlab.knows.dataio.iterators;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.ods.ODSFileParser;
import be.ugent.idlab.knows.dataio.source.ODSSource;
import be.ugent.idlab.knows.dataio.source.Source;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.SQLException;
import java.util.Arrays;

public class ODSSourceIterator extends SourceIterator {
    private static final long serialVersionUID = 4036007304900261485L;
    private final Access access;
    private transient String[] header;
    private transient ODSFileParser parser;
    private transient String[] data;

    public ODSSourceIterator(Access access) throws SQLException, IOException, XMLStreamException {
        this.access = access;
        bootstrap();
    }

    private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException, XMLStreamException, SQLException {
        inputStream.defaultReadObject();
        this.bootstrap();
    }

    private void bootstrap() throws SQLException, IOException, XMLStreamException {
        this.parser = ODSFileParser.newInstance(access.getInputStream());
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
        return new ODSSource(this.header, temp, this.access.getDataTypes());
    }

    @Override
    public void close() {
        // nothing to close
    }
}