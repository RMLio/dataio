package be.ugent.idlab.knows.dataio.iterators;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.source.JSONSource;
import be.ugent.idlab.knows.dataio.source.Source;
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

    /**
     * This method returns a JSON document from an InputStream.
     *
     * @param stream the used InputStream.
     * @return a JSON document.
     * @throws IOException
     */
    public static Object getDocumentFromStream(InputStream stream) throws IOException {
        return Configuration.defaultConfiguration().jsonProvider().parse(stream, "utf-8");
    }

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

    Object getDocumentFromStream(InputStream stream, String contentType) throws IOException {
        if (contentType.equalsIgnoreCase("jsonl")) {
            JsonProvider provider = Configuration.defaultConfiguration().jsonProvider();
            BufferedReader lineReader = new BufferedReader(new InputStreamReader(stream));
            Object items = provider.createArray();
            int index = 0;
            while (lineReader.ready()) {
                provider.setArrayIndex(items, index, provider.parse(lineReader.readLine()));
                index += 1;
            }
            return items;
        } else {
            return getDocumentFromStream(stream);
        }
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
