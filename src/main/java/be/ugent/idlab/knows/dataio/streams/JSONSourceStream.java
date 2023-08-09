package be.ugent.idlab.knows.dataio.streams;

import be.ugent.idlab.knows.dataio.access.Access;
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
    private final String jsonPath;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Iterator<Object> iterator;

    public JSONSourceStream(Access access, String jsonPath) throws SQLException, IOException {
        this.jsonPath = jsonPath;
        JsonSurfer surfer = JsonSurferJackson.INSTANCE;
        this.iterator = surfer.iterator(access.getInputStream(), JsonPathCompiler.compile(this.jsonPath));
    }

    @Override
    public Stream<Source> getStream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this.iterator, 0), false)
                .map(obj -> (ObjectNode) obj)
                .map(objectNode -> mapper.convertValue(objectNode, Map.class))
                .map(map -> new JSONSource(map, "", jsonPath));
    }

    @Override
    public void close() {
        // nothing to close
    }
}
