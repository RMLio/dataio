package be.ugent.idlab.knows.dataio.iterator;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.VirtualAccess;
import be.ugent.idlab.knows.dataio.iterators.CSVSourceIterator;
import be.ugent.idlab.knows.dataio.iterators.JSONSourceIterator;
import be.ugent.idlab.knows.dataio.iterators.SourceIterator;
import be.ugent.idlab.knows.dataio.iterators.XMLSourceIterator;
import be.ugent.idlab.knows.dataio.record.Record;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * An iterator that is pointed at another source must produce what a freshly constructed
 * iterator over that source produces, so that reusing one is only a matter of speed.
 */
public class SourceIteratorResetTest {

    private static Access access(String content) {
        return new VirtualAccess(content.getBytes());
    }

    /** Drains an iterator into something comparable. */
    private static List<String> drain(SourceIterator iterator, Function<Record, String> render) {
        List<String> out = new ArrayList<>();
        while (iterator.hasNext()) {
            out.add(render.apply(iterator.next()));
        }
        return out;
    }

    private void assertResetMatchesFreshIterator(List<String> sources,
                                                 ThrowingFunction<Access, SourceIterator> factory,
                                                 Function<Record, String> render) throws Exception {
        // what a fresh iterator produces for every source
        List<List<String>> expected = new ArrayList<>();
        for (String source : sources) {
            try (SourceIterator fresh = factory.apply(access(source))) {
                expected.add(drain(fresh, render));
            }
        }

        // the same, through a single iterator pointed at each source in turn
        List<List<String>> actual = new ArrayList<>();
        try (SourceIterator reused = factory.apply(access(sources.get(0)))) {
            for (String source : sources) {
                reused.reset(access(source));
                actual.add(drain(reused, render));
            }
        }

        assertEquals(expected, actual);
        // guard against the sources being indistinguishable, which would make this vacuous
        assertTrue(expected.stream().distinct().count() > 1, "sources must differ for this to prove anything");
    }

    @Test
    public void xmlIteratorCanBePointedAtAnotherSource() throws Exception {
        List<String> sources = List.of(
                "<people><person><name>alice</name></person><person><name>bob</name></person></people>",
                "<people><person><name>carol</name></person></people>",
                "<people><person><name>dave</name></person><person><name>erin</name></person></people>"
        );

        assertResetMatchesFreshIterator(sources,
                a -> new XMLSourceIterator(a, "./people/person"),
                record -> record.get("name").getValue().toString());
    }

    @Test
    public void jsonIteratorCanBePointedAtAnotherSource() throws Exception {
        List<String> sources = List.of(
                "{\"people\": [{\"name\": \"alice\"}, {\"name\": \"bob\"}]}",
                "{\"people\": [{\"name\": \"carol\"}]}",
                "{\"people\": [{\"name\": \"dave\"}, {\"name\": \"erin\"}]}"
        );

        assertResetMatchesFreshIterator(sources,
                a -> new JSONSourceIterator(a, "$.people[*]"),
                record -> record.get("name").getValue().toString());
    }

    @Test
    public void csvIteratorCanBePointedAtAnotherSource() throws Exception {
        List<String> sources = List.of(
                "name,city\nalice,Ghent\nbob,Brussels\n",
                "name,city\ncarol,Antwerp\n",
                "name,city\ndave,Bruges\nerin,Leuven\n"
        );

        assertResetMatchesFreshIterator(sources,
                CSVSourceIterator::new,
                record -> record.get("name").getValue() + "@" + record.get("city").getValue());
    }

    @Test
    public void anIteratorThatCannotBeResetSaysSo() throws Exception {
        // the default: an iterator that has not opted in refuses rather than silently
        // returning the previous source's records
        SourceIterator iterator = new be.ugent.idlab.knows.dataio.iterators.JSONLinesSourceIterator(
                access("{\"name\": \"alice\"}\n"), "$");

        assertThrows(UnsupportedOperationException.class, () -> iterator.reset(access("{\"name\": \"bob\"}\n")));
    }

    @FunctionalInterface
    private interface ThrowingFunction<T, R> {
        R apply(T t) throws Exception;
    }
}
