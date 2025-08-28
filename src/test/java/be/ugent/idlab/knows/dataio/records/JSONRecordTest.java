package be.ugent.idlab.knows.dataio.records;

import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.iterators.JSONSourceIterator;
import be.ugent.idlab.knows.dataio.iterators.SourceIterator;
import be.ugent.idlab.knows.dataio.record.Record;
import be.ugent.idlab.knows.dataio.record.RecordValue;
import be.ugent.idlab.knows.dataio.utils.NAMESPACES;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class JSONRecordTest {

    @Test
    public void testTwoDotsIterator() throws Exception{
        List<Object> results;
        try (SourceIterator it = new JSONSourceIterator(new LocalFileAccess("artworks.json", "src/test/resources/json/jsonPathTwoDotsTest", NAMESPACES.XSD + "string"), "$")) {
            results = new ArrayList<>();
            it.forEachRemaining(r -> results.addAll((List<?>)r.get("..children[*].id").getValue()));
        }
        assertEquals(List.of(29L,91L,31L,2815L,177L,95L,97L,273L,544L,2653L,451L,799L), results);
    }

   @Test
    public void testExistingIterationPathNormalValue() throws Exception {
        try (SourceIterator it = new JSONSourceIterator(new LocalFileAccess("null_value.json", "src/test/resources/json", NAMESPACES.XSD + "string"), "$")) {
            Record result = it.next();
            RecordValue value = result.get("$");
            assertTrue(value.isOk());
            assertInstanceOf(LinkedHashMap.class, value.getValue());
        }
    }

    @Test
    public void testExistingIterationPathNullValue() throws Exception {
        try (SourceIterator it = new JSONSourceIterator(new LocalFileAccess("null_value.json", "src/test/resources/json", NAMESPACES.XSD + "string"), "$.null")) {
            Record result = it.next();
            RecordValue value = result.get("@");
            assertTrue(value.isEmpty());
        }
    }

    @Test
    public void testUnExistingIterationPath() throws Exception {
        // A null value
        try (SourceIterator it = new JSONSourceIterator(new LocalFileAccess("null_value.json", "src/test/resources/json", NAMESPACES.XSD + "string"), "$.unexisting")) {
            it.next();
        } catch (NoSuchElementException e) {
            // This should fail
            return;
        }
        fail("NoSuchElementException expected");
    }

    @Test
    public void testExistingReferenceNormalValue() throws Exception{
        try (SourceIterator it = new JSONSourceIterator(new LocalFileAccess("null_value.json", "src/test/resources/json", NAMESPACES.XSD + "string"), "$.array[*]")) {
            Record result = it.next();
            RecordValue value = result.get("two");
            assertTrue(value.isOk());
            assertEquals(2, ((Long)value.getValue()).longValue());
        }
    }

    @Test
    public void testExistingReferenceNullValue() throws Exception{
        try (SourceIterator it = new JSONSourceIterator(new LocalFileAccess("null_value.json", "src/test/resources/json", NAMESPACES.XSD + "string"), "$.array[*]")) {
            Record result = it.next();
            RecordValue value = result.get("null");
            assertTrue(value.isEmpty());
        }
    }

    @Test
    public void testNonExistingReference() throws Exception{
        try (SourceIterator it = new JSONSourceIterator(new LocalFileAccess("null_value.json", "src/test/resources/json", NAMESPACES.XSD + "string"), "$.array[*]")) {
            Record result = it.next();
            RecordValue value = result.get("joske");
            assertTrue(value.isNotFound());
        }
    }
}
