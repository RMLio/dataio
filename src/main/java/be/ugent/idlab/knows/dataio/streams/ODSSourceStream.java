package be.ugent.idlab.knows.dataio.streams;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.ODSSourceIterator;
import be.ugent.idlab.knows.dataio.source.Source;

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
    private ODSSourceIterator iterator;

    /**
     * Opens the source and prepares for streaming
     *
     * @param access access to the file
     */
    @Override
    public void open(Access access) throws SQLException, IOException {
        this.iterator = new ODSSourceIterator();
        this.iterator.open(access);
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
}
