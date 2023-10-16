package be.ugent.idlab.knows.dataio.iterator;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.cores.TestCore;
import be.ugent.idlab.knows.dataio.iterators.HTMLSourceIterator;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class HTMLIteratorTest extends TestCore {

    @Test
    public void evaluate_0000_HTML() throws Exception {
        Access access = makeLocalAccess("/html/0000.html", "", "html", "utf-8");
        try (HTMLSourceIterator htmlSourceIterator = new HTMLSourceIterator(access, "table tbody tr")) {
            assertTrue(evaluate_0000(htmlSourceIterator));
        }
    }

    @Test
    public void evaluate_0001_HTML() throws Exception {
        Access access = makeLocalAccess("/html/0001.html", "", "html", "utf-8");
        try (HTMLSourceIterator htmlSourceIterator = new HTMLSourceIterator(access, "table tbody tr")) {
            assertTrue(evaluate_0001(htmlSourceIterator));
        }
    }

    @Test
    public void evaluate_1001_header_col_missing_html() throws Exception {
        Access access = makeLocalAccess("/html/1001_header_col_missing.html", "", "html", "utf-8");
        try (HTMLSourceIterator htmlSourceIterator = new HTMLSourceIterator(access, "table tbody tr")) {
            //TODO should fail, check if it does
        }
    }

    @Test
    public void evaluate_1001_header_long_html() throws Exception {
        Access access = makeLocalAccess("/html/1001_header_long.html", "", "html", "utf-8");
        try (HTMLSourceIterator htmlSourceIterator = new HTMLSourceIterator(access, "table tbody tr")) {
            assertTrue(evaluate_1001_header_long(htmlSourceIterator));
        }
    }

    @Test
    public void evaluate_1001_header_short_html() throws Exception {
        Access access = makeLocalAccess("/html/1001_header_short.html", "", "html", "utf-8");
        try (HTMLSourceIterator htmlSourceIterator = new HTMLSourceIterator(access, "table tbody tr")) {
            assertTrue(evaluate_1001_header_short(htmlSourceIterator));
        }
    }
}
