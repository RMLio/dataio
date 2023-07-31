package be.ugent.idlab.knows.dataio.iterators;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.csvw.CSVWConfiguration;
import be.ugent.idlab.knows.dataio.source.Source;

import java.io.IOException;
import java.sql.SQLException;

/**
 * An iterator for CSV Sources.
 * Implementation relies on CSVWSourceIterator, as regular CSV is just a special case of CSVW.
 */
public class CSVSourceIterator extends SourceIterator {
    private final CSVWSourceIterator iterator;

    public CSVSourceIterator(Access access) throws SQLException, IOException {
        this.iterator = new CSVWSourceIterator(access, CSVWConfiguration.DEFAULT);
    }

    public Source next() {
        return this.iterator.next();
    }

    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    @Override
    public void close() throws IOException {
        this.iterator.close();
    }
}
