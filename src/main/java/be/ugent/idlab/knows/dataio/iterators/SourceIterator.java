package be.ugent.idlab.knows.dataio.iterators;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.record.Record;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * Iterator over a source. Generates Records from an Access object.
 */
public abstract class SourceIterator implements Iterator<Record>, Serializable, AutoCloseable {
    @Serial
    private static final long serialVersionUID = 7064007069397357197L;

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void forEachRemaining(Consumer<? super Record> action) {
        while (hasNext())
            action.accept(next());
    }

    /**
     * Points this iterator at another source and restarts the iteration, keeping the
     * configuration it was constructed with.
     * <p>
     * Setting a source up costs more than reading it: a parser has to be built and the
     * iterator's expression compiled. A caller that reads many sources with the same
     * configuration, such as one record's worth of data at a time, can construct a single
     * iterator and point it at each source in turn rather than construct one per source.
     * <p>
     * An iterator that is reused is stateful, so it belongs to whoever reset it until it
     * is drained: it cannot be shared between threads.
     *
     * @param access the source to read next
     * @throws UnsupportedOperationException if this iterator cannot be pointed at another
     *                                       source, which is the default
     */
    public void reset(Access access) throws Exception {
        throw new UnsupportedOperationException(
                "%s cannot be pointed at another source".formatted(this.getClass().getSimpleName()));
    }

    public void close() throws IOException {}
}
