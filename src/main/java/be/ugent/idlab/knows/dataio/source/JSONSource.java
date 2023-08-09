package be.ugent.idlab.knows.dataio.source;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.JsonPathException;
import net.minidev.json.JSONArray;
import org.jsfr.json.compiler.JsonPathCompiler;
import org.jsfr.json.path.PathOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is a specific implementation of a source for JSON.
 * Every source corresponds with a JSON object in a data source.
 */
public class JSONSource extends Source {
    private final Object document; // JSON object
    private final String tag; // what iterator was used to obtain this.document
    private final String path; // what specific path was taken to arrive at this.document
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    private List<String> compiledPath;

    public JSONSource(Object document, String tag) {
        this(document, tag, "");
    }

    public JSONSource(Object document, String tag, String path) {
        this.document = document;
        this.tag = tag;
        this.path = path;
    }

    /**
     * This method returns the objects for a reference (JSONPath) in the record.
     *
     * @param value the reference for which objects need to be returned.
     * @return a list of objects for the reference.
     */
    @Override
    public List<Object> get(String value) {
        if (value.startsWith("_")) {
            return processMagicProperty(value);
        }

        List<Object> results = new ArrayList<>();

        if (value.contains(" ")) {
            value = String.format("['%s']", value);
        }

        if (!value.contains("$")) {
            value = String.format("$.%s", value);
        }

//        // We put simple values between square brackets to make sure no non-escaped shenanigans happen.
//        if (!value.contains("[") && !value.contains(".") && !value.equals("@")) {
//            value = "['" + value + "']";
//        } else if (value.equals("@")) {
//            value = "" ;
//        } else {
//            value = "." + value;
//        }

        // TODO do we need to be smarter that this? Below isn't complete yet, but also doesn't seem necessary
//        String[] valueParts = value.split("\\.");
//        StringBuilder escapedValue = new StringBuilder();
//        for (String valuePart : valueParts) {
//            // This JSONPath library specifically cannot handle keys with commas, so we need to escape it
//            String escapedValuePart = valuePart.replaceAll(",", "\\\\,");
//            if (!(escapedValuePart.startsWith("["))) {
//                escapedValue.append("['").append(escapedValuePart).append("']");
//            } else {
//                escapedValue.append(escapedValuePart);
//            }
//        }

//        // This JSONPath library specifically cannot handle keys with commas, so we need to escape it
//        String fullValue = (this.path + value).replaceAll(",", "\\\\,");

        try {
            //Object t = JsonPath.read(document, fullValue);
            Object t = JsonPath.read(this.document, value);

            if (t instanceof JSONArray) {
                JSONArray array = (JSONArray) t;
                ArrayList<String> tempList = new ArrayList<>();

                for (Object o : array) {
                    if (o != null) {
                        tempList.add(o.toString());
                    }
                }

                results.add(tempList);
            } else {
                if (t != null) {
                    results.add(t.toString());
                }
            }
        } catch (JsonPathException e) {
            logger.warn("{} for path {} ", e.getMessage(), this.path + value, e);
            return null;
        }

        return results;
    }

    private List<Object> processMagicProperty(String reference) {
        if (reference.startsWith("_PATH")) {
            return processPath(reference);
        }

        throw new IllegalArgumentException(String.format("Unknown magic property %s", reference));
    }

    /**
     * Processes the magic property _PATH.
     * This property is the reverse path taken to arrive at the Source.
     * Referencing _PATH returns whole path, indexing (e.g. _PATH[0]) will return a specific index.
     * @param reference reference of form _PATH[{index}]
     * @return the specific path, possibly indexed.
     */
    private List<Object> processPath(String reference) {
        if (this.path.isEmpty()) {
            return List.of(""); // early empty return if no specific path was given
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
     * @return magic property _PATH in required format.
     */
    private List<Object> constructPath() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(this.compiledPath.get(0));
        for (int i = 1; i < this.compiledPath.size(); i++) {
            builder.append(",");
            builder.append(this.compiledPath.get(i));
        }

        builder.append("]");

        return List.of(builder.toString());
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
     * @param index index in path
     * @return the element at _PATH[index]
     */
    private List<Object> processPathIndex(int index) {
        return List.of(this.compiledPath.get(index));
    }

    public String getTag() {
        return tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JSONSource that = (JSONSource) o;
        return path.equals(that.path) && document.equals(that.document);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), path, document);
    }
}
