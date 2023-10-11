package be.ugent.idlab.knows.dataio.iterator;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.cores.TestCore;
import be.ugent.idlab.knows.dataio.iterators.JSONLinesSourceIterator;
import be.ugent.idlab.knows.dataio.iterators.JSONSourceIterator;
import be.ugent.idlab.knows.dataio.record.Record;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JSONIteratorTest extends TestCore {
    @Test
    public void evaluate_0000_JSON() throws SQLException, IOException {
        Access access = makeLocalAccess("/json/0000.json", "", "json", "utf-8");
        JSONSourceIterator jsonSourceIterator = new JSONSourceIterator(access, "$.students[*]");

        assertTrue(evaluate_0000(jsonSourceIterator));
    }

    @Test
    public void evaluate_0001_JSON() throws SQLException, IOException {
        Access access = makeLocalAccess("/json/0001.json", "", "json", "utf-8");
        JSONSourceIterator jsonSourceIterator = new JSONSourceIterator(access, "$.pubs[*]");

        assertTrue(evaluate_0001(jsonSourceIterator));
    }

    @Test
    public void evaluate_multiple_sources() throws SQLException, IOException {
        Map<String, Object> e1 = new LinkedHashMap<>() {{
            put("ID", "10");
            put("Sport", "100");
            put("Name", "Venus Williams");
        }};

        Map<String, Object> e2 = new LinkedHashMap<>() {{
            put("ID", "20");
            put("Name", "Demi Moore");
        }};

        Access access = makeLocalAccess("/json/multiple_sources.json", "", "json", "utf-8");
        try (JSONSourceIterator iterator = new JSONSourceIterator(access, "$.students[*]")) {
            assertTrue(compareIterator(iterator, List.of(e1, e2)));
        }
    }

    @Test
    public void evaluate_empty_array() throws SQLException, IOException {
        Access access = makeLocalAccess("/json/empty_array.json", "", "json", "utf-8");
        try (JSONSourceIterator iterator = new JSONSourceIterator(access, "$[*]")) {
            assertFalse(iterator.hasNext());
        }
    }

    @Test
    public void evaluate_quoted_multiword_keys() throws SQLException, IOException {
        Access access = makeLocalAccess("/json/multiword_keys.json", "", "json", "utf-8");
        try (JSONSourceIterator iterator = new JSONSourceIterator(access, "$.*")) {
            Record s1 = iterator.next();
            assertEquals("BO", s1.get("ISO 3166").get(0));

            Record s2 = iterator.next();
            assertEquals("IE", s2.get("\"ISO 3166\"").get(0));
        }
    }

    @Test
    public void evaluate_path_to_array() throws SQLException, IOException {
        Access access = makeLocalAccess("/json/array.json", "", "json", "utf-8");
        try (JSONSourceIterator iterator = new JSONSourceIterator(access, "$[*].ingredients[*]")) {
            Record s = iterator.next();
            assertEquals("garlic", s.get("@").get(0));
        }
    }

    /**
     * Tests the replacement of .[*] construction in the iteratorPath
     */
    @Test
    public void evaluate_path_replacement() throws SQLException, IOException {
        Access access = makeLocalAccess("/json/array.json", "", "json", "utf-8");
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
    public void evaluate_json_lines() throws SQLException, IOException {
        List<Object> expected = List.of("10", "Venus", "11", "null", "12", "Serena");
        Access access = makeLocalAccess("/json/data.jsonl", "", "jsonl", "utf-8");
        try (JSONLinesSourceIterator iterator = new JSONLinesSourceIterator(access, "$.*")) {
            List<Object> actual = new ArrayList<>();
            iterator.forEachRemaining(s -> actual.add(s.get("@").get(0)));

            assertEquals(expected, actual);
        }
    }
}
