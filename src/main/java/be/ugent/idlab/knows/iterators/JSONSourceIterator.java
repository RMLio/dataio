package be.ugent.idlab.knows.iterators;

import be.ugent.idlab.knows.access.Access;
import be.ugent.idlab.knows.source.JSONSource;
import be.ugent.idlab.knows.source.Source;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.spi.json.JsonProvider;
import org.jsfr.json.JsonSurfer;
import org.jsfr.json.JsonSurferJackson;
import org.jsfr.json.ResumableParser;
import org.jsfr.json.SurfingConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Map;

/**
 * This class is a JSonSourceiterator that allows the iteration of json data.
 */
public class JSONSourceIterator extends SourceIterator {
    private final JsonSurfer surfer = JsonSurferJackson.INSTANCE;
    private Object currentObject;
    private String currentPath;
    private ResumableParser parser;

    public void open(Access access, String string_iterator) throws SQLException, IOException {
        SurfingConfiguration config = JsonSurferJackson.INSTANCE
                .configBuilder()
                .bind(string_iterator, (value, context) -> {
                    this.currentObject = value;
                    this.currentPath = context.getJsonPath();
                    context.pause();
                }).build();
        this.parser = surfer.createResumableParser(access.getInputStream(), config);
        this.parser.parse();
    }

    @Override
    public boolean hasNext() {
        return parser.resume();
    }

    @Override
    public Source next() {
        ObjectMapper mapper = new ObjectMapper();

        return new JSONSource(mapper.convertValue(this.currentObject, Map.class), this.currentPath);
    }
}
