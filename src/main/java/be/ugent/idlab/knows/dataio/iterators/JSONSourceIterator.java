package be.ugent.idlab.knows.dataio.iterators;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.source.JSONSource;
import be.ugent.idlab.knows.dataio.source.Source;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.spi.json.JsonProvider;
import org.jsfr.json.*;
import org.jsfr.json.compiler.JsonPathCompiler;
import org.jsfr.json.path.JsonPath;

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
        // replace any occurences of .[ (e.g. $.[*]) with [ (such that we get $[*])
        this.iterationPath = iterationPath.replaceAll("\\.\\[", "[");
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
        JsonSurfer surfer = JsonSurferJackson.INSTANCE;

        SurfingConfiguration config = surfer
                .configBuilder()
                .bind(iterationPath, (value, context) -> {
                    this.match = value;
                    this.currentPath = context.getJsonPath();
                    this.hasMatch = true;
                    context.pause();
                })
                .build();
        this.parser = surfer.createResumableParser(this.inputStream, config);
        this.parser.parse();
    }

    private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException, SQLException {
        inputStream.defaultReadObject();
        this.bootstrap();
    }

    @Override
    public boolean hasNext() {
        return hasMatch || this.parser.resume() && hasMatch;
    }

    @Override
    public Source next() {
        if (this.hasNext()) {
            Object match = this.match;
            String path = this.currentPath;
            this.match = null;
            this.currentPath = null;
            this.hasMatch = false;

            if (! (match instanceof ValueNode)) {
                ObjectMapper mapper = new ObjectMapper();
                match = mapper.convertValue(match, Map.class);
            }

            return new JSONSource(match, path);
        }

        throw new NoSuchElementException();
    }

    @Override
    public void close() throws IOException {
        this.inputStream.close();
    }
}
