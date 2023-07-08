package be.ugent.idlab.knows.dataio.streams;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.ODSSourceIterator;
import be.ugent.idlab.knows.dataio.source.Source;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Implementation of the ODS stream. Streams values based on the ODS file provided in Access.
 * Internally, this implementation relies on the ODSSourceIterator, which already reads the files the streaming way
 */
public class ODSSourceStream implements SourceStream {
    private final ODSSourceIterator iterator;

    public ODSSourceStream(Access access) throws XMLStreamException, SQLException, IOException {
        this.iterator = new ODSSourceIterator(access);
    }

    /**
     * Provides a Stream object to be consumed
     *
     * @return a Stream object
     */
    @Override
    public Stream<Source> getStream() {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(this.iterator, Spliterator.IMMUTABLE | Spliterator.NONNULL), false);
    }

    @Override
    public void close() {
        this.iterator.close();
    }
}
