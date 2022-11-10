package be.ugent.idlab.knows.streams;

import be.ugent.idlab.knows.access.Access;
import be.ugent.idlab.knows.source.Source;

import java.io.IOException;
import java.sql.SQLException;
import java.util.stream.Stream;

public interface SourceStream {

    /**
     * Opens the source and prepares for streaming
     * @param access access to the file
     */
    void open(Access access) throws SQLException, IOException;

    /**
     * Provides a Stream object to be consumed
     *
     * @return a Stream object
     */
    Stream<Source> getStream();
}
