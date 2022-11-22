package be.ugent.idlab.knows.dataio.streams;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.CSVWSourceIterator;
import be.ugent.idlab.knows.dataio.source.Source;
import com.opencsv.CSVParserBuilder;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CSVWSourceStream implements SourceStream {

    private final CSVWSourceIterator iterator;
    private final List<String> nulls;
    private final boolean skipHeader;
    private final boolean trim;
    private final CSVParserBuilder parser;

    public CSVWSourceStream(CSVParserBuilder parser, List<String> nulls, boolean skipHeader, boolean trim) {
        this.iterator = new CSVWSourceIterator();
        this.parser = parser;
        this.nulls = nulls;
        this.skipHeader = skipHeader;
        this.trim = trim;
    }

    @Override
    public void open(Access access) throws SQLException, IOException {
        this.iterator.open(access, parser, nulls, skipHeader, trim);
    }

    @Override
    public Stream<Source> getStream() {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(this.iterator, Spliterator.ORDERED), true);
    }
}
