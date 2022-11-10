package be.ugent.idlab.knows.streams;

import be.ugent.idlab.knows.access.Access;
import be.ugent.idlab.knows.iterators.CSVSourceIterator;
import be.ugent.idlab.knows.source.Source;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CSVSourceStream implements SourceStream {
    // we can reuse the already present iterator, since OpenCSV reads the CSV the streaming way, without loading full file into memory
    private CSVSourceIterator iterator;

    @Override
    public void open(Access access) throws SQLException, IOException {
        this.iterator = new CSVSourceIterator();
        iterator.open(access);
    }

    @Override
    public Stream<Source> getStream() {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(this.iterator, Spliterator.ORDERED), true);
    }
}
