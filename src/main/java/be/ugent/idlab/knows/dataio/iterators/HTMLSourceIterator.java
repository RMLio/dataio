package be.ugent.idlab.knows.dataio.iterators;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.source.HTMLSource;
import be.ugent.idlab.knows.dataio.source.Source;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * This class is a HTMLSourceIterator that creates HTML records.
 */
public class HTMLSourceIterator extends SourceIterator {

    private static final Logger logger = LoggerFactory.getLogger(HTMLSourceIterator.class);
    private final Iterator<Element> iterator;
    private List<String> headers;

    public HTMLSourceIterator(Access access, String stringIterator) throws SQLException, IOException {
        this.iterator = Jsoup.parse(access.getInputStream(), "UTF-8", "http://example.com/")
                .select(stringIterator).iterator();
        if (this.iterator.hasNext()) {
            headers = this.iterator.next().select("th").stream().map(Element::text).collect(Collectors.toList());
        }
    }

    @Override
    public Source next() {
        if (hasNext()) {
            return new HTMLSource(iterator.next(), headers);
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public void close() {
        // nothing to close
    }
}
