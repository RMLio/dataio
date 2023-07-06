package be.ugent.idlab.knows.dataio.streams;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.source.Source;

import java.io.IOException;
import java.sql.SQLException;
import java.util.stream.Stream;

public interface SourceStream extends AutoCloseable {
    /**
     * Provides a Stream object to be consumed
     *
     * @return a Stream object
     */
    Stream<Source> getStream();

    default Stream<Source> getParallelStream() {
        return this.getStream().parallel();
    }
}
