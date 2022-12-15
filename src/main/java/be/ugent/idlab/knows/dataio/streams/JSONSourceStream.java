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
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class JSONSourceStream implements SourceStream {
    private final JsonSurfer surfer = JsonSurferJackson.INSTANCE;
    private final String jsonPath;

    private Iterator<Object> iterator;

    private final ObjectMapper mapper = new ObjectMapper();

    public JSONSourceStream(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    @Override
    public void open(Access access) throws SQLException, IOException {
        this.iterator = surfer.iterator(access.getInputStream(), JsonPathCompiler.compile(this.jsonPath));
    }

    @Override
    public Stream<Source> getStream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this.iterator, 0), false)
                .map(obj -> (ObjectNode) obj)
                .map(objectNode -> mapper.convertValue(objectNode, Map.class))
                .map(map -> new JSONSource(map, jsonPath));
    }
}
