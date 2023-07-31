package be.ugent.idlab.knows.dataio.streams;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.CSVSourceIterator;
import be.ugent.idlab.knows.dataio.source.Source;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CSVSourceStream implements SourceStream {
    private static final long serialVersionUID = 7550892039319544071L;
    private final CSVSourceIterator iterator;

    public CSVSourceStream(Access access) throws SQLException, IOException {
        this.iterator = new CSVSourceIterator(access);
    }

    @Override
    public Stream<Source> getStream() {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false);
    }

    @Override
    public void close() throws IOException {
        this.iterator.close();
    }
}
