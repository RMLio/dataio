package be.ugent.idlab.knows.source;

import be.ugent.idlab.knows.access.Access;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is a record factory that creates HTML records.
 */
public class HTMLSourceIterator implements SourceIterator {

    private Iterator<Element> iterator;
    private List<String> headers;

    public HTMLSourceIterator(Access access, String iterator) {
        try {
            this.iterator = Jsoup.parse(access.getInputStream(), "UTF-8", "http://example.com/")
                    .select(iterator).iterator();
            if (this.iterator.hasNext()) {
                headers = this.iterator.next().select("th").stream().map(Element::text).collect(Collectors.toList());
            }
        } catch (IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Source nextSource() {
        return new HTMLSource(iterator.next(), headers);
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }
}
