package be.ugent.idlab.knows.dataio.iterator;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.cores.TestCore;
import be.ugent.idlab.knows.dataio.iterators.JSONSourceIterator;
import be.ugent.idlab.knows.dataio.source.Source;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
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
            Source s1 = iterator.next();
            assertEquals(s1.get("ISO 3166").get(0), "BO");

            Source s2 = iterator.next();
            assertEquals(s2.get("\"ISO 3166\"").get(0), "IE");
        }
    }

    @Test
    public void evaluate_path_to_array() throws SQLException, IOException {
        Access access = makeLocalAccess("/json/array.json", "", "json", "utf-8");
        try (JSONSourceIterator iterator = new JSONSourceIterator(access, "$[*].ingredients[*]")) {
            Source s = iterator.next();
            System.out.println(s.get("@"));
        }

    }
}
