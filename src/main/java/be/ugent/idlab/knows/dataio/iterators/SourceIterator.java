package be.ugent.idlab.knows.dataio.iterators;

import be.ugent.idlab.knows.dataio.source.Source;

import java.io.Serializable;
import java.util.Iterator;
import java.util.function.Consumer;

public abstract class SourceIterator implements Iterator<Source>, Serializable, AutoCloseable {
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void forEachRemaining(Consumer<? super Source> action) {
        while (hasNext())
            action.accept(next());
    }
}
