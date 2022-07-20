package be.ugent.idlab.knows.iterators;

import be.ugent.idlab.knows.access.Access;
import be.ugent.idlab.knows.source.HTMLSource;
import be.ugent.idlab.knows.source.Source;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * This class is a record factory that creates HTML records.
 */
public class HTMLSourceIterator extends SourceIterator {

    private Iterator<Element> iterator;
    private List<String> headers;

    /**
     * Opens the files using the access object and initiates the iterator and header
     * @param access the corresponding access object
     * @param string_iterator string value used in the parser
     */
    public void open(Access access, String string_iterator) {
        try {
            this.iterator = Jsoup.parse(access.getInputStream(), "UTF-8", "http://example.com/")
                    .select(string_iterator).iterator();
            if (this.iterator.hasNext()) {
                headers = this.iterator.next().select("th").stream().map(Element::text).collect(Collectors.toList());
            }
        } catch (IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Source next() {
        if(hasNext()){
            return new HTMLSource(iterator.next(), headers);
        } else{
            throw new NoSuchElementException();
        }
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }
}
