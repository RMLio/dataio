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

import java.io.*;
import java.sql.SQLException;
import java.util.Map;

/**
 * This class is a JSONSourceIterator that allows the iteration of JSON data.
 */
public class JSONSourceIterator extends SourceIterator {
    private static final long serialVersionUID = 5727357114356164542L;
    private final Access access;
    private final String iterationPath;
    private transient ResumableParser parser;
    private transient Object currentObject;
    private transient String currentPath;

    public JSONSourceIterator(Access access, String iterationPath) throws SQLException, IOException {
        this.access = access;
        this.iterationPath = iterationPath;
        this.bootstrap();
    }

    /**
     * This method returns a JSON document from an InputStream.
     *
     * @param stream the used InputStream.
     * @return a JSON document.
     */
    public static Object getDocumentFromStream(InputStream stream) {
        return Configuration.defaultConfiguration().jsonProvider().parse(stream, "utf-8");
    }

    private void bootstrap() throws SQLException, IOException {
        SurfingConfiguration config = JsonSurferJackson.INSTANCE
                .configBuilder()
                .bind(iterationPath, (value, context) -> {
                    this.currentObject = value;
                    this.currentPath = context.getJsonPath();
                    context.pause();
                }).build();

        JsonSurfer surfer = JsonSurferJackson.INSTANCE;
        this.parser = surfer.createResumableParser(access.getInputStream(), config);
        this.parser.parse();
    }

    private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException, SQLException {
        inputStream.defaultReadObject();
        this.bootstrap();
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

        return new JSONSource(mapper.convertValue(this.currentObject, Map.class), this.iterationPath, this.currentPath);
    }

    @Override
    public void close() {
        // nothing to close
    }
}
