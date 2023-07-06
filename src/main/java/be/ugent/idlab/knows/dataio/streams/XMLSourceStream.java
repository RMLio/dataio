package be.ugent.idlab.knows.dataio.streams;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.XMLSourceIterator;
import be.ugent.idlab.knows.dataio.source.Source;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class XMLSourceStream implements SourceStream {
    private final String xpath;
    private final XMLSourceIterator iterator;

    public XMLSourceStream(String xpath) {
        this.xpath = xpath;
        this.iterator = new XMLSourceIterator();
    }

    /**
     * Opens the source and prepares for streaming
     *
     * @param access access to the file
     */
    public void open(Access access) throws SQLException, IOException {
        this.iterator.open(access, xpath);
    }

    /**
     * Provides a Stream object to be consumed
     *
     * @return a Stream object
     */
    @Override
    public Stream<Source> getStream() {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(this.iterator, Spliterator.ORDERED),
                false);
    }

    @Override
    public void close() throws Exception {

    }
}
