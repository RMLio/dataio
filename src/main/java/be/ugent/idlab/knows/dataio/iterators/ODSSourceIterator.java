package be.ugent.idlab.knows.dataio.iterators;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.exceptions.BadHeaderException;
import be.ugent.idlab.knows.dataio.source.ODSSource;
import be.ugent.idlab.knows.dataio.source.Source;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class representing an Iterator for ODS documents.
 * Warning: Loads the entire document into memory, not suitable for large ODS documents!
 */
public class ODSSourceIterator extends SourceIterator {
    private static final long serialVersionUID = 4036007304900261485L;
    private final Access access;
    private transient Iterator<ODSSource> sources;

    public ODSSourceIterator(Access access) throws SQLException, IOException {
        this.access = access;
        bootstrap();
    }

    private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException, XMLStreamException, SQLException {
        inputStream.defaultReadObject();
        this.bootstrap();
    }

    private void bootstrap() throws SQLException, IOException {
        List<ODSSource> sources = new ArrayList<>();
        try (InputStream is = this.access.getInputStream()) {
            Document document;

            // SpreadsheetDocument.loadDocument(is) throws simply an Exception
            // nested try is used to catch this separately
            try {
                document = SpreadsheetDocument.loadDocument(is);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            List<Table> tables = document.getTableList();
            for (Table t : tables) {
                Row header = t.getRowByIndex(0);
                // check the header
                for (int i = 0; i < header.getCellCount(); i++) {
                    if (header.getCellByIndex(i).getStringValue().isEmpty()) {
                        throw new BadHeaderException(access.getAccessPath());
                    }
                }

                for (int i = 1; i < t.getRowList().size(); i++) {
                    Row row = t.getRowByIndex(i);
                    sources.add(new ODSSource(header, row));
                }
            }

            this.sources = sources.iterator();
        }
    }

    @Override
    public boolean hasNext() {
        return this.sources.hasNext();
    }

    @Override
    public Source next() {
        return this.sources.next();
    }

    @Override
    public void close() {
        // nothing to close
    }
}