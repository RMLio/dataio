package be.ugent.idlab.knows.dataio.iterators;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.source.CSVSource;
import be.ugent.idlab.knows.dataio.source.Source;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import org.apache.commons.io.input.BOMInputStream;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class CSVSourceIterator extends SourceIterator {
    private final Access access;
    private final CSVReader reader;
    private final Iterator<String[]> iterator;
    private final String[] header;

    public CSVSourceIterator(Access access) throws SQLException, IOException {
        this.access = access;

        try (BOMInputStream in = new BOMInputStream(access.getInputStream())) {
            this.reader = new CSVReaderBuilder(new InputStreamReader(in))
                    .withSkipLines(0)
                    .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                    .build();
            this.iterator = this.reader.iterator();
            this.header = iterator.next();
        }
    }

    public Source next() {
        if (iterator.hasNext()) {
            return new CSVSource(header, iterator.next(), this.access.getDataTypes());
        } else {
            throw new NoSuchElementException();
        }
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public void close() throws IOException {
        this.reader.close();
    }
}
