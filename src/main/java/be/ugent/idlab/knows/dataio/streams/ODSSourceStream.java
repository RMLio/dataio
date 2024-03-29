package be.ugent.idlab.knows.dataio.streams;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.ODSSourceIterator;
import be.ugent.idlab.knows.dataio.record.Record;

import java.io.Serial;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Implementation of the ODS stream. Streams values based on the ODS file provided in Access.
 * Internally, this implementation relies on the ODSSourceIterator.
 * Warning: this class consumes the entire source into memory.
 */
public class ODSSourceStream implements SourceStream {
    @Serial
    private static final long serialVersionUID = -8550401945125763790L;
    private final ODSSourceIterator iterator;

    public ODSSourceStream(Access access) throws Exception {
        this.iterator = new ODSSourceIterator(access);
    }

    /**
     * Provides a Stream object to be consumed
     *
     * @return a Stream object
     */
    @Override
    public Stream<Record> getStream() {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(this.iterator, Spliterator.IMMUTABLE | Spliterator.NONNULL), false);
    }

    @Override
    public void close() {
        this.iterator.close();
    }
}
