package be.ugent.idlab.knows.dataio.cores;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.record.Record;
import be.ugent.idlab.knows.dataio.record.RecordValue;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestCore {

    public boolean evaluate_0000(Iterator<Record> iterator) {
        Map<String, Object> expected = Map.of(
                "ID", "10",
                "Name", "Venus"
        );

        return compareIterator(iterator, List.of(expected));
    }

    public boolean evaluate_0001(Iterator<Record> iterator) {
        Map<String, Object> expectedRecord1 = Map.of(
                "name", "Trollekelder",
                "description", "Beer café in the shadows of the St James' church",
                "address", "Bij Sint-Jacobs 17 9000 Gent Belgium"
        );

        Map<String, Object> expectedRecord2 = Map.of(
                "name", "Hot Club Gent",
                "description", "Live jazz concerts in an intimate setting",
                "address", "Schuddevisstraatje 2 - Groentenmarkt 9000 Gent Belgium"
        );
        Map<String, Object> expectedRecord3 = Map.of(
                "name", "Jan van Gent",
                "description", "Quirky local pub with a popular summer terrace",
                "address", "Annonciadenstraat 1 9000 Gent Belgium"
        );

        return compareIterator(iterator, List.of(expectedRecord1, expectedRecord2, expectedRecord3));
    }

    public boolean evaluate_0002_BOM(Iterator<Record> iterator) {
        Map<String, Object> r1 = Map.of(
                "Id", "1",
                "Name", "Sasha",
                "Translation", "Саша"
        );

        Map<String, Object> r2 = Map.of(
                "Id", "2",
                "Name", "Michael",
                "Translation", "Миша"
        );

        Map<String, Object> r3 = Map.of(
                "Id", "3",
                "Name", "David",
                "Translation", "Давид"
        );

        return compareIterator(iterator, List.of(r1, r2, r3));
    }

    public boolean evaluate_1001_header_long(Iterator<Record> iterator) {
        Map<String, Object> expected1 = Map.of(
                "name", "Trollekelder",
                "description", "Beer café in the shadows of the St James' church",
                "address", "Bij Sint-Jacobs 17 9000 Gent Belgium",
                "extra", ""
        );

        Map<String, Object> expected2 = Map.of(
                "name", "Hot Club Gent",
                "description", "Live jazz concerts in an intimate setting",
                "address", "Schuddevisstraatje 2 - Groentenmarkt 9000 Gent Belgium",
                "extra", ""
        );
        Map<String, Object> expected3 = Map.of(
                "name", "Jan van Gent",
                "description", "Quirky local pub with a popular summer terrace",
                "address", "Annonciadenstraat 1 9000 Gent Belgium",
                "extra", ""
        );

        return compareIterator(iterator, List.of(expected1, expected2, expected3));
    }

    public boolean evaluate_1001_header_short(Iterator<Record> iterator) {
        Map<String, Object> expected1 = Map.of(
                "name", "Trollekelder",
                "description", "Beer café in the shadows of the St James' church"
        );
        Map<String, Object> expected2 = Map.of(
                "name", "Hot Club Gent",
                "description", "Live jazz concerts in an intimate setting"
        );
        Map<String, Object> expected3 = Map.of(
                "name", "Jan van Gent",
                "description", "Quirky local pub with a popular summer terrace"
        );

        return compareIterator(iterator, List.of(expected1, expected2, expected3));
    }

    public boolean evaluate_empty(Iterator<Record> iterator) {
        return !iterator.hasNext();
    }

    public boolean compareIterator(Iterator<Record> iterator, Set<Record> expectedRecords) {
        int counter = 0;
        while (iterator.hasNext()) {
            counter++;
            Record record = iterator.next();
            if (!expectedRecords.contains(record)) {
                return false;
            }

        }
        return expectedRecords.size() == counter;
    }

    public Access makeLocalAccess(String inputFile, String base, String type, Charset encoding) {
        return new LocalFileAccess(getClass().getResource(inputFile).getPath(), base, type, encoding);
    }

    /**
     * Compares the iterator with the values in the expected list, according to the expected header
     *
     * @param iterator iterator with actual values
     * @param list     list of expected values
     */
    public boolean compareIterator(Iterator<Record> iterator, List<Map<String, Object>> list) {
        int counter = 0;

        while (iterator.hasNext()) {
            Record record = iterator.next();
            if (!compareMap(record, list)) {
                return false; // source doesn't line up with expected values
            }
            counter++;
        }
        return counter == list.size();
    }

    /**
     * Asserts if the source conforms to a particular map
     *
     * @param record    source to be checked
     * @param checkMaps list of maps to check against
     * @return true if the source conforms to a map in checkMaps
     */
    private boolean compareMap(Record record, List<Map<String, Object>> checkMaps) {
        for (Map<String, Object> map : checkMaps) {
            if (compareMapParticular(record, map)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Asserts if the source conforms to the checkMap
     *
     * @param record source to be checked
     * @param map    map to be compared against
     * @return true if all the fields in the map appear correctly in source
     */
    private boolean compareMapParticular(Record record, Map<String, Object> map) {
        for (String key : map.keySet()) {
            RecordValue recordValue = record.get(key);

            if (recordValue.isEmpty()) { // empty list returned, value not in source
                if (!map.get(key).equals("")) { // no value expected here
                    return false;
                }
            } else {
                Object value = recordValue.getValue();
                if (value instanceof List<?>) {
                    value = ((List<?>) value).get(0);
                }
                if (!value.toString().equals(map.get(key))) {   // Everything is treated as String here
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Prints out the statistics of memory usage of the code run.
     */
    public static void printMemoryStatistics() {
        Runtime runtime = Runtime.getRuntime();
        int mb = 1024 * 1024;

        // Suggest garbage collector to clean up
        for (int i = 0; i < 10; i++) {
            System.gc();
        }

        System.out.println("Used memory: " + ((runtime.totalMemory() - runtime.freeMemory()) / mb) + " MB");
        System.out.println("Free memory: " + runtime.freeMemory() / mb + " MB");
        System.out.println("Total memory: " + runtime.totalMemory() / mb + " MB");
        System.out.println("Max memory: " + runtime.maxMemory() / mb + " MB");
    }

    /**
     * Functional interface for evaluator functions
     */
    protected interface Evaluator {
        boolean evaluate(Iterator<Record> iterator);
    }

    /**
     * Generic method that simulates serialization of the object.
     * Returns a new instance of the passed object that should be exactly the same as argument
     * @param object object to be serialized
     * @return a new instance of the argument, deserialized
     * @param <T> Type of object
     */
    public <T extends Serializable> T simulateSerialization(T object) {
        byte[] bytes = SerializationUtils.serialize(object);

        return SerializationUtils.deserialize(bytes);
    }
}