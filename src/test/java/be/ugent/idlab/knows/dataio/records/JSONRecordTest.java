package be.ugent.idlab.knows.dataio.records;

import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.iterators.JSONSourceIterator;
import be.ugent.idlab.knows.dataio.iterators.SourceIterator;
import be.ugent.idlab.knows.dataio.utils.NAMESPACES;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
