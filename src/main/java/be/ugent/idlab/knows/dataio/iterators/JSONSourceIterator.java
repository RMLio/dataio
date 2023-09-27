package be.ugent.idlab.knows.dataio.iterators;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.source.JSONSource;
import be.ugent.idlab.knows.dataio.source.Source;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.spi.json.JsonProvider;
import org.jsfr.json.*;
import org.jsfr.json.compiler.JsonPathCompiler;

import java.io.*;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * This class is a JSONSourceIterator that allows the iteration of JSON data.
 */
public class JSONSourceIterator extends SourceIterator {
    private static final long serialVersionUID = 5727357114356164542L;
    private final Access access;
    private final String iterationPath;
    private transient ResumableParser parser;
    private transient InputStream inputStream;
    private transient String currentPath;
    private transient Object match = null;
    private boolean hasMatch = false;

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
        this.inputStream = access.getInputStream();
        SurfingConfiguration config = JsonSurferJackson.INSTANCE
                .configBuilder()
                .bind(iterationPath, (value, context) -> {
                    this.match = value;
                    this.currentPath = context.getJsonPath();
                    this.hasMatch = true;
                    context.pause();
                })
                .build();

        JsonSurfer surfer = JsonSurferJackson.INSTANCE;

        this.parser = surfer.createResumableParser(this.inputStream, config);
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
        return hasMatch || this.parser.resume() && hasMatch;
    }

    @Override
    public Source next() {
        if (hasNext()) {
            Object match = this.match;
            this.match = null;
            this.currentPath = null;
            this.hasMatch = false;

            ObjectMapper mapper = new ObjectMapper();
            return new JSONSource(mapper.convertValue(match, Map.class), this.currentPath);
        }

        throw new NoSuchElementException();
    }

    @Override
    public void close() throws IOException {
        this.inputStream.close();
    }
}
