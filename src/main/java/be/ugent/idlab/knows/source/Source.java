package be.ugent.idlab.knows.source;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class Source {
    public static final Logger logger = LoggerFactory.getLogger(Source.class);

    /**
     * This method returns the objects for a reference in the source.
     * @param value the reference for which objects need to be returned.
     * @return a list of objects for the reference.
     */
    public abstract List<Object> get(String value);

    /**
     * This method returns the datatype of a reference in the source.
     * @param value the reference for which the datatype needs to be returned.
     * @return the IRI of the datatype.
     */
    public String getDataType(String value) {
        return null;
    }

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public int hashCode(){
        return 1;
    };

}
