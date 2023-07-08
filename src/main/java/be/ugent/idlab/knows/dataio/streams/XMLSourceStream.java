package be.ugent.idlab.knows.dataio.streams;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.XMLSourceIterator;
import be.ugent.idlab.knows.dataio.source.Source;
import net.sf.saxon.s9api.SaxonApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class XMLSourceStream implements SourceStream {
    private final XMLSourceIterator iterator;

    public XMLSourceStream(Access access, String xpath) throws SQLException, IOException, SaxonApiException {
        this.iterator = new XMLSourceIterator(access, xpath);
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
    public void close() {
        this.iterator.close();
    }
}
