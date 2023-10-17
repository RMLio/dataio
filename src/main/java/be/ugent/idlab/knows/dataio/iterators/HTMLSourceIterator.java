package be.ugent.idlab.knows.dataio.iterators;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.record.HTMLRecord;
import be.ugent.idlab.knows.dataio.record.Record;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * This class is a HTMLSourceIterator that creates HTML records.
 */
public class HTMLSourceIterator extends SourceIterator {
    private static final long serialVersionUID = -79397539726939643L;
    private final Access access;
    private final String stringIterator;
    private transient Iterator<Element> iterator;
    private transient List<String> headers;

    public HTMLSourceIterator(Access access, String stringIterator) throws Exception {
        this.access = access;
        this.stringIterator = stringIterator;
        this.bootstrap();
    }

    /**
     * Instantiates transient fields. This code needs to be run both at construction time and after deserialization
     *
     * @throws IOException  can be thrown due to the consumption of the input stream. Same for SQLException.
     */
    private void bootstrap() throws Exception {
        try (InputStream inputStream = this.access.getInputStream()) {
            this.iterator = Jsoup.parse(inputStream, "UTF-8", "http://example.com/")
                    .select(this.stringIterator)
                    .iterator();
            if (this.iterator.hasNext()) {
                this.headers = this.iterator.next()
                        .select("th")
                        .stream()
                        .map(Element::text)
                        .collect(Collectors.toList());
            }
        }
    }

    private void readObject(ObjectInputStream inputStream) throws Exception {
        inputStream.defaultReadObject();
        bootstrap();
    }

    @Override
    public Record next() {
        if (hasNext()) {
            return new HTMLRecord(iterator.next(), headers);
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public void close() throws IOException {
        // do nothing
    }
}
