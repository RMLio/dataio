package be.ugent.idlab.knows.dataio.records;

import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.iterators.XMLSourceIterator;
import be.ugent.idlab.knows.dataio.record.Record;
import be.ugent.idlab.knows.dataio.record.RecordValue;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;


public class XMLRecordTest {

    @Test
    public void testExistingIterationPathNormalValue() throws Exception {
        try (XMLSourceIterator it = new XMLSourceIterator(new LocalFileAccess("empty.xml", "src/test/resources/xml"), "/students/student" )) {
            Record result = it.next();
            RecordValue value = result.get("Name");
            assertTrue(value.isOk());
            assertEquals(List.of("Venus"), value.getValue());
        }
    }

    @Test
    public void testExistingIterationPathEmptyValue() throws Exception {
        try (XMLSourceIterator it = new XMLSourceIterator(new LocalFileAccess("empty.xml", "src/test/resources/xml"), "/students/student" )) {
            Record result = it.next();
            RecordValue value = result.get("Hobby");
            assertTrue(value.isEmpty());
        }
    }


    @Test
    public void testUnExistingIterationPath() throws Exception {
        try (XMLSourceIterator it = new XMLSourceIterator(new LocalFileAccess("empty.xml", "src/test/resources/xml"), "/unexisting" )) {
            it.next();
        } catch (NoSuchElementException e) {
            // This should fail
            return;
        }
        fail("NoSuchElementException expected");
    }

    @Test
    public void testNonExistingReference() throws Exception{
        try (XMLSourceIterator it = new XMLSourceIterator(new LocalFileAccess("empty.xml", "src/test/resources/xml"), "/students/student" )) {
            Record result = it.next();
            RecordValue value = result.get("Unexisting");
            assertTrue(value.isNotFound());
        }
    }

}
