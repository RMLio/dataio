package be.ugent.idlab.knows.dataio.record;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * A representation of a source that can be used for a mapping
 * Every source contains references to values. This is done differently per Source type.
 */
public abstract class Record {
    public static final Logger logger = LoggerFactory.getLogger(Record.class);

    /**
     * This method returns the objects for a reference in the source.
     *
     * @param value the reference for which objects need to be returned.
     * @return a list of objects for the reference.
     */
    public abstract List<Object> get(String value);

    public Map<String, String> getDataTypes() {
        return null;
    }

    /**
     * This method returns the datatype of a reference in the source.
     *
     * @param value the reference for which the datatype needs to be returned.
     * @return the IRI of the datatype.
     */
    public String getDataType(String value) {
        return null;
    }
}
