package be.ugent.idlab.knows.iterator;

import be.ugent.idlab.knows.TestCore;
import be.ugent.idlab.knows.iterators.HTMLSourceIterator;
import org.junit.Test;

public class HTMLTest extends TestCore {

    @Test
    public void evaluate_0000_HTML(){
        HTMLSourceIterator htmlSourceIterator = new HTMLSourceIterator();
        htmlSourceIterator.open(makeLocalAccess("/html/0000.html"), "table tbody tr");

        evaluate_0000(htmlSourceIterator, false);

//        Element element = new Element("tr").appendChild(new Element("td").text("Venus"));
//        List<String> headers = List.of("Name");
//        HTMLSource source = new HTMLSource(element, headers);
//
//        compareIterator(htmlSourceIterator, Set.of(source));
    }

    @Test
    public void evaluate_0001_HTML(){
        HTMLSourceIterator htmlSourceIterator = new HTMLSourceIterator();
        htmlSourceIterator.open(makeLocalAccess("/html/0001.html"), "table tbody tr");

        evaluate_0001(htmlSourceIterator);
    }
}
