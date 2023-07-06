package be.ugent.idlab.knows.dataio.iterators;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.source.CSVSource;
import be.ugent.idlab.knows.dataio.source.Source;
import com.opencsv.CSVIterator;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import org.apache.commons.io.input.BOMInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.*;

public class CSVSourceIterator extends SourceIterator {

    private static final Logger logger = LoggerFactory.getLogger(CSVSourceIterator.class);
    private final Access access;

    private final CSVReader reader;

    private Iterator<String[]> iterator;
    private String[] header;

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

    /**
     * Opens the files using the access object and initiates the iterator and header
     *
     * @param access the corresponding access object
     */
    public void open(Access access) {
        try (BOMInputStream inputStream = new BOMInputStream(access.getInputStream())) {
            iterator = new CSVReaderBuilder(new InputStreamReader(inputStream))
                    .withSkipLines(0)
                    .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                    .build().iterator();
            header = iterator.next();
            checkHeader(header);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkHeader(String[] header) {
        for (String cell : header) {
            if (cell == null) {
                logger.warn("Header contains null values");
            }
        }
        Set<String> set = new HashSet<>(Arrays.asList(header));
        if (set.size() != header.length) {
            logger.warn("Header contains duplicates");
        }

    }

    /**
     * Gets the next value of the iterator and creates a source with this value
     *
     * @return
     */
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
