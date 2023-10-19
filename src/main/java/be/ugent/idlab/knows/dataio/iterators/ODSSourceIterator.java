package be.ugent.idlab.knows.dataio.iterators;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.exceptions.HeaderEmptyValuesException;
import be.ugent.idlab.knows.dataio.record.ODSRecord;
import be.ugent.idlab.knows.dataio.record.Record;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class representing an Iterator for ODS documents.
 * Warning: Loads the entire document into memory, not suitable for large ODS documents!
 */
public class ODSSourceIterator extends SourceIterator {
    @Serial
    private static final long serialVersionUID = 4036007304900261485L;
    private final Access access;
    private transient Iterator<ODSRecord> records;

    public ODSSourceIterator(Access access) throws Exception {
        this.access = access;
        bootstrap();
    }

    @Serial
    private void readObject(ObjectInputStream inputStream) throws Exception {
        inputStream.defaultReadObject();
        this.bootstrap();
    }

    /**
     * Instantiates transient fields. This code needs to be run both at construction time and after deserialization
     *
     * @throws Exception  can be thrown due to the consumption of the input stream.
     */
    private void bootstrap() throws Exception {
        List<ODSRecord> sources = new ArrayList<>();
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
                        throw new HeaderEmptyValuesException(access.getAccessPath());
                    }
                }

                for (int i = 1; i < t.getRowList().size(); i++) {
                    Row row = t.getRowByIndex(i);
                    sources.add(new ODSRecord(header, row));
                }
            }

            this.records = sources.iterator();
        }
    }

    @Override
    public boolean hasNext() {
        return this.records.hasNext();
    }

    @Override
    public Record next() {
        return this.records.next();
    }

    @Override
    public void close() {
        // nothing to close
    }
}