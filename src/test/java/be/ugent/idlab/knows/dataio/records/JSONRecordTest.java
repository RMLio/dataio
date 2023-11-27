package be.ugent.idlab.knows.dataio.records;

import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.iterators.JSONSourceIterator;
import be.ugent.idlab.knows.dataio.utils.NAMESPACES;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class JSONRecordTest {

    @Test
    public void testTwoDotsIterator() throws Exception{
        JSONSourceIterator it = new JSONSourceIterator(new LocalFileAccess("artworks.json", "src/test/resources/json/jsonPathTwoDotsTest", NAMESPACES.XSD + "string"), "$");
        ArrayList<String> results = new ArrayList<>();
        it.forEachRemaining(r -> {
            results.add(r.get("..children[*].id").toString());
        });
        assertTrue(results.contains("[[29,91,31,2815,177,95,97,273,544,2653,451,799]]"));
    }
}
