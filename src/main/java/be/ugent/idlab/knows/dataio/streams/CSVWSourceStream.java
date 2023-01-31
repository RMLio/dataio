package be.ugent.idlab.knows.dataio.streams;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.CSVWSourceIterator;
import be.ugent.idlab.knows.dataio.iterators.csvw.CSVWConfiguration;
import be.ugent.idlab.knows.dataio.source.Source;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CSVWSourceStream implements SourceStream {
    private final CSVWConfiguration config;
    private final CSVWSourceIterator iterator;

    public CSVWSourceStream(CSVWConfiguration config) {
        this.config = config;
        this.iterator = new CSVWSourceIterator();
    }

    @Override
    public void open(Access access) throws SQLException, IOException {
        this.iterator.open(access, this.config);
    }

    @Override
    public Stream<Source> getStream() {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(this.iterator, Spliterator.ORDERED), false);
    }
}
