package be.ugent.idlab.knows.dataio.streams;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.JSONSourceIterator;
import be.ugent.idlab.knows.dataio.record.Record;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Stream of JSON records.
 * Internally relies on JSONSourceIterator for record generation.
 */
public class JSONSourceStream implements SourceStream {
    private static final long serialVersionUID = -4481189880575751432L;
    private final JSONSourceIterator iterator;

    public JSONSourceStream(Access access, String jsonPath) throws Exception {
        this.iterator = new JSONSourceIterator(access, jsonPath);
    }

    @Override
    public Stream<Record> getStream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this.iterator, 0), false);
    }

    @Override
    public void close() throws IOException {
        this.iterator.close();
    }
}
