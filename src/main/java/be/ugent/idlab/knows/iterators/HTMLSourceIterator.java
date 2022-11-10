package be.ugent.idlab.knows.iterators;

import be.ugent.idlab.knows.access.Access;
import be.ugent.idlab.knows.source.HTMLSource;
import be.ugent.idlab.knows.source.Source;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is a HTMLSourceIterator that creates HTML records.
 */
public class HTMLSourceIterator extends SourceIterator {

    private static final Logger logger = LoggerFactory.getLogger(HTMLSourceIterator.class);
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
            checkHeader(headers);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void checkHeader(List<String> header) {
        Set<String> set = new HashSet<>();

        for(String cell: header){
            set.add(cell);
            if(cell == null){
                logger.warn("Header contains null values");
            }
        }

        if (set.size() != header.size()){
            logger.warn("Header contains duplicates");
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
