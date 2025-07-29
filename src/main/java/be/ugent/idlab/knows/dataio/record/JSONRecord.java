package be.ugent.idlab.knows.dataio.record;

import com.fasterxml.jackson.databind.node.ValueNode;
import com.jayway.jsonpath.*;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import org.jsfr.json.compiler.JsonPathCompiler;
import org.jsfr.json.path.PathOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is a specific implementation of a source for JSON.
 * Every source corresponds with a JSON object in a data source.
 */
public class JSONRecord extends Record {
    // Configuration for Jayway to provide stable outputs
    static {
        Configuration.setDefaults(new Configuration.Defaults() {

            private final JsonProvider jsonProvider = Configuration.defaultConfiguration().jsonProvider();
            private final MappingProvider mappingProvider = Configuration.defaultConfiguration().mappingProvider();

            @Override
            public JsonProvider jsonProvider() {
                return jsonProvider;
            }

            @Override
            public Set<Option> options() {
                return Set.of(Option.DEFAULT_PATH_LEAF_TO_NULL);
            }

            @Override
            public MappingProvider mappingProvider() {
                return mappingProvider;
            }
        });
    }

    private final Object document; // JSON object
    private final String tag; // what iterator was used to obtain this.document
    private final String path; // what specific path was taken to arrive at this.document
    private final int index; // what index in an array thee element is at
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    private List<String> compiledPath;

    public JSONRecord(Object document, String tag) {
        this(document, tag, "");
    }

    public JSONRecord(Object document, String tag, String path) {
        this(document, tag, path, -1);
    }

    public JSONRecord(Object document, String tag, String path, int index) {
        this.document = document;
        this.tag = tag;
        this.path = path;
        this.index = index;
    }

    /**
     * This method returns the objects for a reference (JSONPath) in the record.
     *
     * @param reference the reference for which objects need to be returned.
     * @return a list of objects for the reference.
     */
    @Override
    public RecordValue get(String reference) {
        if (reference.startsWith("\\_")) {
            return processMagicProperty(reference);
        }

        // if JSONPath was so specific that it reduced the document to a single entry, only acceptable reference is @
        if (this.document instanceof ValueNode && reference.equals("@")) {
            String v = ((ValueNode) this.document).asText();
            return RecordValue.ok(v);
        }

        if (reference.startsWith("\"") && reference.endsWith("\"")) {
            reference = reference.substring(1, reference.length() - 1);
        }


        if (reference.contains(" ")) {
            reference = String.format("['%s']", reference);
        }

        if (!reference.contains("$")) {
            reference = reference.startsWith(".") ? String.format("$%s", reference) : String.format("$.%s", reference);
        }

        if (reference.equals("@")) {
            reference = "";
        }

        try {
            Object result = JsonPath.read(this.document, reference);
            if (result != null) {
                return RecordValue.ok(result);
            } else {
                return RecordValue.empty();
            }

        } catch (PathNotFoundException e) {
            String message = "JSONPath '" + this.path + reference + "': " + e.getMessage();
            logger.warn(message, e);
            return RecordValue.notFound(message);
        } catch (JsonPathException e) {
            String message = "JSONPath '" + this.path + reference + "': " + e.getMessage();
            logger.warn(message, e);
            return RecordValue.error(message);
        }
    }

    private RecordValue processMagicProperty(String reference) {
        if (reference.startsWith("\\_PATH")) {
            return processPath(reference);
        } else {
            return RecordValue.error(String.format("Unknown magic property %s", reference));
        }
    }

    /**
     * Processes the magic property _PATH.
     * This property is the reverse path taken to arrive at the Source.
     * Referencing _PATH returns whole path, indexing (e.g. _PATH[0]) will return a specific index.
     *
     * @param reference reference of form _PATH[{index}]
     * @return the specific path, possibly indexed.
     */
    private RecordValue processPath(String reference) {
        if (this.path.isEmpty()) {
            return RecordValue.empty(); // early empty return if no specific path was given
        }

        // ensure path is only compiled ONCE
        if (this.compiledPath == null) {
            compilePath();
        }

        Pattern pattern = Pattern.compile("_PATH\\[(\\d+)]");
        Matcher matcher = pattern.matcher(reference);
        if (matcher.find()) {
            return processPathIndex(Integer.parseInt(matcher.group(1)));
        }

        return constructPath();
    }

    /**
     * Constructs the path in the "[{reverse path}] format.
     *
     * @return magic property _PATH in required format.
     */
    private RecordValue constructPath() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(this.compiledPath.get(0));
        for (int i = 1; i < this.compiledPath.size(); i++) {
            builder.append(",");
            builder.append(this.compiledPath.get(i));
        }

        builder.append("]");

        return RecordValue.ok(builder.toString());
    }

    /**
     * Compiles path and makes it ready for processing.
     * This is done due to the specific path being passed around as a String
     */
    private void compilePath() {
        org.jsfr.json.path.JsonPath jsonPath = JsonPathCompiler.compile(this.path);
        this.compiledPath = new ArrayList<>();

        for (int i = jsonPath.pathDepth() - 1; i >= 0; i--) {
            PathOperator operator = jsonPath.get(i);
            String operatorString = operator.toString();
            if (operator.getType().equals(PathOperator.Type.ARRAY)) { // something like [X]
                int length = operatorString.length();
                this.compiledPath.add(operatorString.substring(1, length - 1)); // X
            } else if (operator.getType().equals(PathOperator.Type.OBJECT)) { // something like .X
                this.compiledPath.add(operatorString.substring(1)); // X
            }
        }
    }

    /**
     * Processes _PATH property with a specific index
     *
     * @param index index in path
     * @return the element at _PATH[index]
     */
    private RecordValue processPathIndex(int index) {
        return RecordValue.ok(this.compiledPath.get(index));
    }

    public String getTag() {
        return tag;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JSONRecord that = (JSONRecord) o;
        return path.equals(that.path) && document.equals(that.document);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), path, document);
    }
}
