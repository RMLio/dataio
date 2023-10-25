package be.ugent.idlab.knows.dataio.streams;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.XMLSourceIterator;
import be.ugent.idlab.knows.dataio.record.Record;

import java.io.Serial;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Stream of XML records.
 * Internally relies on XMLSourceIterator for record generation.
 * Warning: this class consumes the entire source into memory.
 */
public class XMLSourceStream implements SourceStream {
    @Serial
    private static final long serialVersionUID = -5444782156545294885L;
    private final XMLSourceIterator iterator;

    public XMLSourceStream(Access access, String xpath) throws Exception {
        this.iterator = new XMLSourceIterator(access, xpath);
    }

    /**
     * Provides a Stream object to be consumed
     *
     * @return a Stream object
     */
    @Override
    public Stream<Record> getStream() {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(this.iterator, Spliterator.ORDERED),
                false);
    }

    @Override
    public void close() {
        this.iterator.close();
    }
}
