package be.ugent.idlab.knows.streams;

import be.ugent.idlab.knows.access.Access;
import be.ugent.idlab.knows.iterators.XMLSourceIterator;
import be.ugent.idlab.knows.source.Source;

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
    @Override
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
                true);
    }
}
