package be.ugent.idlab.knows.dataio.streams;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.CSVWSourceIterator;
import be.ugent.idlab.knows.dataio.iterators.csvw.CSVWConfiguration;
import be.ugent.idlab.knows.dataio.record.Record;

import java.io.IOException;
import java.io.Serial;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Stream of CSV records from a CSVW source.
 * Internally relies on CSVWSourceIterator for record generation.
 */
public class CSVWSourceStream implements SourceStream {
    @Serial
    private static final long serialVersionUID = -9054328656428580251L;
    private final CSVWSourceIterator iterator;

    public CSVWSourceStream(Access access, CSVWConfiguration config) throws Exception {
        this.iterator = new CSVWSourceIterator(access, config);
    }

    @Override
    public Stream<Record> getStream() {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(this.iterator, Spliterator.ORDERED), false);
    }

    @Override
    public void close() throws IOException {
        this.iterator.close();
    }
}
