package be.ugent.idlab.knows.iterator;

import be.ugent.idlab.knows.TestCore;
import be.ugent.idlab.knows.source.ExcelSource;
import be.ugent.idlab.knows.source.ExcelSourceIterator;
import be.ugent.idlab.knows.source.HTMLSource;
import be.ugent.idlab.knows.source.HTMLSourceIterator;
import org.jsoup.nodes.Element;
import org.junit.Test;

import java.util.List;
import java.util.Set;

public class HTMLTest extends TestCore {

    @Test
    public void evaluate_0000_HTML(){
        Element element = new Element("tr").appendChild(new Element("td").text("Venus"));
        List<String> headers = List.of("Name");
        HTMLSource source = new HTMLSource(element, headers);

        compareIterator(new HTMLSourceIterator(makeLocalAccess("/html/0000.html"), "table tbody tr"), Set.of(source));
    }
}
