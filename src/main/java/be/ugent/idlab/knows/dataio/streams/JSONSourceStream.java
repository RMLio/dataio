package be.ugent.idlab.knows.dataio.streams;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.JSONSourceIterator;
import be.ugent.idlab.knows.dataio.source.JSONSource;
import be.ugent.idlab.knows.dataio.source.Source;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jsfr.json.JsonSurfer;
import org.jsfr.json.JsonSurferJackson;
import org.jsfr.json.compiler.JsonPathCompiler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class JSONSourceStream implements SourceStream {
    private static final long serialVersionUID = -4481189880575751432L;
    private final JSONSourceIterator iterator;

    public JSONSourceStream(Access access, String jsonPath) throws SQLException, IOException {
        this.iterator = new JSONSourceIterator(access, jsonPath);
    }

    @Override
    public Stream<Source> getStream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this.iterator, 0), false);
    }

    @Override
    public void close() {
        this.iterator.close();
    }
}
