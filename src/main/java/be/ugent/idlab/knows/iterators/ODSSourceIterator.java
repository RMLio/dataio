package be.ugent.idlab.knows.iterators;

import be.ugent.idlab.knows.access.Access;
import be.ugent.idlab.knows.source.CSVSource;
import be.ugent.idlab.knows.source.Source;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import org.apache.commons.io.input.BOMInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ODSSourceIterator extends SourceIterator {

    private static final Logger logger = LoggerFactory.getLogger(CSVSourceIterator.class);
    private Iterator<String[]> iterator;

    //    private Row header;
    private String[] header;
    private Map<String, String> dataTypes;

    /**
     * This function partly loads the full file in at once (to read the tables).
     * Opens the files using the access object and initiates the tableIterator, iterator and header.
     *
     * @param access the corresponding access object
     */
    public void open(Access access) {
        dataTypes = access.getDataTypes();
        try (BOMInputStream inputStream = new BOMInputStream(access.getInputStream())) {
            // little hack due to how inputStream works
            iterator = new CSVReaderBuilder(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
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

    @Override
    public Source next() {
        // has next updates the iterators
        if (iterator.hasNext()) {
            // little hack due to how inputStream works
            return new CSVSource(header, iterator.next(), dataTypes);
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }
}
