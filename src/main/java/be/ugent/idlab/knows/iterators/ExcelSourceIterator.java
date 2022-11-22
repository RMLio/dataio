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


public class ExcelSourceIterator extends SourceIterator {

    private static final Logger logger = LoggerFactory.getLogger(ExcelSourceIterator.class);
    private java.util.Iterator<org.apache.poi.ss.usermodel.Sheet> workbookIterator;
    private Iterator<String[]> iterator;
    private String[] header;
    private Map<String, String> dataTypes;


    /**
     * Opens the files using the access object and initiates the workbookIterator, iterator and header.
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

    @Override
    public Source next() {
        if (iterator.hasNext()) {
            // little hack due to how inputStream works
            return new CSVSource(header, iterator.next(), dataTypes);
        } else {
            throw new NoSuchElementException();
        }
    }


    private void checkHeader(String[] header) throws Exception {
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
    public boolean hasNext() {
        return iterator.hasNext();
    }

}
