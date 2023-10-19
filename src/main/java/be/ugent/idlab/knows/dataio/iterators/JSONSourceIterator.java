package be.ugent.idlab.knows.dataio.iterators;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.record.JSONRecord;
import be.ugent.idlab.knows.dataio.record.Record;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.jayway.jsonpath.Configuration;
import org.jopendocument.util.StringInputStream;
import org.jsfr.json.JsonSurfer;
import org.jsfr.json.JsonSurferJackson;
import org.jsfr.json.ResumableParser;
import org.jsfr.json.SurfingConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * This class is a JSONSourceIterator that allows the iteration of JSON data.
 */
public class JSONSourceIterator extends SourceIterator {
    @Serial
    private static final long serialVersionUID = 5727357114356164542L;
    private final Access access;
    private final String iterationPath;
    private transient ResumableParser parser;
    private transient InputStream inputStream;
    private transient String currentPath;
    private transient Object match = null;
    private boolean hasMatch = false;

    public JSONSourceIterator(Access access, String iterationPath) throws Exception {
        this.access = access;
        // replace any occurrences of .[ (e.g. $.[*]) with [ (such that we get $[*])
        this.iterationPath = iterationPath.replaceAll("\\.\\[", "[");
        this.bootstrap();
    }

    public JSONSourceIterator(String json, String iterationPath) throws Exception {
        // small hack to use the existing constructor
        this(new Access() {
            @Override
            public InputStream getInputStream() {return new StringInputStream(json);}
            @Override
            public Map<String, String> getDataTypes() {return null;}

            @Override
            public String getContentType() {return null;}

            @Override
            public String getAccessPath() {return null;}
        }, iterationPath);
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

    /**
     * Instantiates transient fields. This code needs to be run both at construction time and after deserialization
     *
     * @throws Exception  can be thrown due to the consumption of the input stream.
     */
    private void bootstrap() throws Exception {
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

    @Serial
    private void readObject(ObjectInputStream inputStream) throws Exception {
        inputStream.defaultReadObject();
        this.bootstrap();
    }

    @Override
    public boolean hasNext() {
        return hasMatch || this.parser.resume() && hasMatch;
    }

    @Override
    public Record next() {
        if (this.hasNext()) {
            Object match = this.match;
            String path = this.currentPath;
            this.match = null;
            this.currentPath = null;
            this.hasMatch = false;

            if (!(match instanceof ValueNode)) {
                ObjectMapper mapper = new ObjectMapper();
                match = mapper.convertValue(match, Map.class);
            }

            return new JSONRecord(match, this.iterationPath, path);
        }

        throw new NoSuchElementException();
    }

    @Override
    public void close() throws IOException {
        this.inputStream.close();
    }
}
