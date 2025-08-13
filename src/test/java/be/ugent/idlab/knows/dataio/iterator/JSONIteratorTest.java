package be.ugent.idlab.knows.dataio.iterator;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.cores.TestCore;
import be.ugent.idlab.knows.dataio.iterators.JSONLinesSourceIterator;
import be.ugent.idlab.knows.dataio.iterators.JSONSourceIterator;
import be.ugent.idlab.knows.dataio.record.JSONRecord;
import be.ugent.idlab.knows.dataio.record.Record;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JSONIteratorTest extends TestCore {
    @Test
    public void evaluate_0000_JSON() throws Exception {
        Access access = makeLocalAccess("/json/0000.json", "", "json", StandardCharsets.UTF_8);
        JSONSourceIterator jsonSourceIterator = new JSONSourceIterator(access, "$.students[*]");

        assertTrue(evaluate_0000(jsonSourceIterator));
    }

    @Test
    public void evaluate_0001_JSON() throws Exception {
        Access access = makeLocalAccess("/json/0001.json", "", "json", StandardCharsets.UTF_8);
        JSONSourceIterator jsonSourceIterator = new JSONSourceIterator(access, "$.pubs[*]");

        assertTrue(evaluate_0001(jsonSourceIterator));
    }

    @Test
    public void evaluate_multiple_sources() throws Exception {
        Map<String, Object> e1 = new LinkedHashMap<>() {{
            put("ID", "10");
            put("Sport", "100");
            put("Name", "Venus Williams");
        }};

        Map<String, Object> e2 = new LinkedHashMap<>() {{
            put("ID", "20");
            put("Name", "Demi Moore");
        }};

        Access access = makeLocalAccess("/json/multiple_sources.json", "", "json", StandardCharsets.UTF_8);
        try (JSONSourceIterator iterator = new JSONSourceIterator(access, "$.students[*]")) {
            assertTrue(compareIterator(iterator, List.of(e1, e2)));
        }
    }

    @Test
    public void evaluate_empty_array() throws Exception {
        Access access = makeLocalAccess("/json/empty_array.json", "", "json", StandardCharsets.UTF_8);
        try (JSONSourceIterator iterator = new JSONSourceIterator(access, "$[*]")) {
            assertFalse(iterator.hasNext());
        }
    }

    /**
     * Tests the magic property path indexing
     */
    @Test
    public void testMagicPropertyPathIndexing() throws Exception {
        Access access = new LocalFileAccess("json/people.json", "src/test/resources", "json");
        try (JSONSourceIterator iterator = new JSONSourceIterator(access, "$.people[*]")) {
            assertTrue(iterator.hasNext());
            JSONRecord record = (JSONRecord) iterator.next();
            // sanity check
            assertEquals(List.of("John"), record.get("firstName").getValue());

            // grab the whole path
            assertEquals("[0,people]", record.get("\\_PATH").getValue());
            // index the path
            assertEquals("people", record.get("\\_PATH[1]").getValue());
            assertFalse(iterator.hasNext());
        }
    }

    @Test
    public void evaluate_quoted_multiword_keys() throws Exception {
        Access access = makeLocalAccess("/json/multiword_keys.json", "", "json", StandardCharsets.UTF_8);
        try (JSONSourceIterator iterator = new JSONSourceIterator(access, "$.*")) {
            Record s1 = iterator.next();
            assertEquals(List.of("BO"), s1.get("ISO 3166").getValue());

            Record s2 = iterator.next();
            assertEquals(List.of("IE"), s2.get("\"ISO 3166\"").getValue());
        }
    }

    @Test
    public void evaluate_path_to_array() throws Exception {
        Access access = makeLocalAccess("/json/array.json", "", "json", StandardCharsets.UTF_8);
        try (JSONSourceIterator iterator = new JSONSourceIterator(access, "$[*].ingredients[*]")) {
            Record s = iterator.next();
            assertEquals("garlic", s.get("@").getValue());
        }
    }

    /**
     * Tests the replacement of .[*] construction in the iteratorPath
     */
    @Test
    public void evaluate_path_replacement() throws Exception {
        Access access = makeLocalAccess("/json/array.json", "", "json", StandardCharsets.UTF_8);
        try (
                JSONSourceIterator it1 = new JSONSourceIterator(access, "$.[*]");
                JSONSourceIterator it2 = new JSONSourceIterator(access, "$[*]")
        ) {
            while (it1.hasNext()) {
                assertTrue(it2.hasNext());
                assertEquals(it1.next(), it2.next());
            }
        }
    }

    /**
     * Tests the JSONLines iterator
     */
    @Test
    public void evaluate_json_lines() throws Exception {
        List<Object> expected = List.of("10", "Venus", "11", "null", "12", "Serena");
        Access access = makeLocalAccess("/json/data.jsonl", "", "jsonl", StandardCharsets.UTF_8);
        try (JSONLinesSourceIterator iterator = new JSONLinesSourceIterator(access, "$.*")) {
            List<Object> actual = new ArrayList<>();
            iterator.forEachRemaining(s -> actual.add(s.get("@").getValue()));

            assertEquals(expected, actual);
        }
    }

    /**
     * Tests referencing the real _PATH reference.
     * To obtain the real property, the _ must be escaped
     */
    @Test
    public void testMagicPropertyEscapedPath() throws Exception {
        Access access = new LocalFileAccess("json/people.json", "src/test/resources", "json");
        try (JSONSourceIterator iterator = new JSONSourceIterator(access, "$.people[*]")) {
            assertTrue(iterator.hasNext());
            JSONRecord record = (JSONRecord) iterator.next();
            // real property
            assertEquals(List.of("foo"), record.get("_PATH").getValue());
            // magic property
            assertEquals("[0,people]", record.get("\\_PATH").getValue());
            assertTrue(record.get("\\\\_PATH").isEmpty());
        }
    }

    @Test
    public void evaluate_nested_array() throws Exception {
        Access access = makeLocalAccess("/json/nested_array.json", "", "json", StandardCharsets.UTF_8);
        try(JSONSourceIterator jsonSourceIterator = new JSONSourceIterator(access, "$.main_array[*]")) {
            while (jsonSourceIterator.hasNext()) {
                Record source = jsonSourceIterator.next();
                List<?> names = (List<?>) source.get("names").getValue();
                List<List<String>> expected = List.of(List.of("Jos", "Jef"));
                assertEquals(expected, names);
                System.out.println();
            }
        }
    }
}
