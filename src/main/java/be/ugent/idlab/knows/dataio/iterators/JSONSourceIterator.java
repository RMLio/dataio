package be.ugent.idlab.knows.dataio.iterators;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.VirtualAccess;
import be.ugent.idlab.knows.dataio.record.JSONRecord;
import be.ugent.idlab.knows.dataio.record.Record;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.jayway.jsonpath.Configuration;
import org.jsfr.json.JsonSurfer;
import org.jsfr.json.JsonSurferJackson;
import org.jsfr.json.ResumableParser;
import org.jsfr.json.SurfingConfiguration;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.SQLException;
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
    private int arrayIndex = 0;

    public JSONSourceIterator(Access access, String iterationPath) throws SQLException, IOException, ParserConfigurationException, TransformerException {
        this.access = access;
        // replace any occurences of .[ (e.g. $.[*]) with [ (such that we get $[*])
        this.iterationPath = iterationPath.replaceAll("\\.\\[", "[");
        this.bootstrap();
    }

    public JSONSourceIterator(String json, String iterationPath) throws SQLException, IOException, ParserConfigurationException, TransformerException {
        this(new VirtualAccess(json.getBytes()), iterationPath);
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
     * @throws IOException  can be thrown due to the consumption of the input stream. Same for SQLException.
     * @throws SQLException
     */
    private void bootstrap() throws SQLException, IOException, ParserConfigurationException, TransformerException {
        this.inputStream = access.getInputStream();

        JsonSurfer surfer = JsonSurferJackson.INSTANCE;

        SurfingConfiguration config = surfer
                .configBuilder()
                .bind(iterationPath, (value, context) -> {
                    this.match = value;
                    this.currentPath = context.getJsonPath();
                    this.hasMatch = true;
                    this.arrayIndex = context.getCurrentArrayIndex();
                    context.pause();
                })
                .build();
        this.parser = surfer.createResumableParser(this.inputStream, config);
        this.parser.parse();
    }

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

            return new JSONRecord(match, this.iterationPath, path, this.arrayIndex);
        }

        throw new NoSuchElementException();
    }

    @Override
    public void close() throws IOException {
        this.inputStream.close();
    }
}
