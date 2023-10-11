package be.ugent.idlab.knows.dataio.record;

import com.fasterxml.jackson.databind.node.ValueNode;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.JsonPathException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class is a specific implementation of a source for JSON.
 * Every source corresponds with a JSON object in a data source.
 */
public class JSONRecord extends Record {

    private final String path;
    private final Object document;
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public JSONRecord(Object document, String path) {
        this.path = path;
        this.document = document;
    }

    /**
     * This method returns the objects for a reference (JSONPath) in the record.
     *
     * @param value the reference for which objects need to be returned.
     * @return a list of objects for the reference.
     */
    @Override
    public List<Object> get(String value) {
        List<Object> results = new ArrayList<>();

        // if JSONPath was so specific that it reduced the document to a single entry, only acceptable reference is @
        if (this.document instanceof ValueNode && value.equals("@")) {
            String v = ((ValueNode) this.document).asText();
            return List.of(v);
        }

        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }


        if (value.contains(" ")) {
            value = String.format("['%s']", value);
        }

        if (!value.contains("$")) {
            value = String.format("$.%s", value);
        }

        if (value.equals("@")) {
            value = "";
        }

        try {
            Object t = JsonPath.read(this.document, value);

            if (t instanceof ArrayList) {
                ArrayList<Object> tCast = (ArrayList<Object>) t;
                ArrayList<String> tempList = new ArrayList<>();

                for (Object o : tCast) {
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
        }

        return results;
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
