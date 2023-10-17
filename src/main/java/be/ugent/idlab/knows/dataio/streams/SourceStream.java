package be.ugent.idlab.knows.dataio.streams;

import be.ugent.idlab.knows.dataio.record.Record;

import java.io.Serializable;
import java.util.stream.Stream;

/**
 * Interface defining a stream of records from a particular source.
 */
public interface SourceStream extends AutoCloseable, Serializable {
    /**
     * Provides a Stream object to be consumed
     *
     * @return a Stream object
     */
    Stream<Record> getStream();

    default Stream<Record> getParallelStream() {
        return this.getStream().parallel();
    }
}
