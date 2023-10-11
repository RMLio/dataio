package be.ugent.idlab.knows.dataio.iterators;

import be.ugent.idlab.knows.dataio.record.Record;

import java.io.Serializable;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * Iterator over a source. Generates Records from an Access object.
 */
public abstract class SourceIterator implements Iterator<Record>, Serializable, AutoCloseable {
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
}
