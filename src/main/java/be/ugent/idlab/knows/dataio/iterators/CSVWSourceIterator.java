package be.ugent.idlab.knows.dataio.iterators;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.source.CSVSource;
import be.ugent.idlab.knows.dataio.source.Source;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import org.apache.commons.io.input.BOMInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;

public class CSVWSourceIterator extends SourceIterator {

    private static final Logger logger = LoggerFactory.getLogger(CSVWSourceIterator.class);

    private com.opencsv.CSVParserBuilder csvParser = new CSVParserBuilder().withIgnoreLeadingWhiteSpace(true);
    private Charset csvCharset = StandardCharsets.UTF_8;

    private List<String> nulls;
    private Iterator<String[]> iterator;
    private String[] header;
    private Map<String, String> dataTypes;
    private boolean trim;

    /**
     * Opens the files using the access object and initiate the iterator, header, nulls list, csvParser and trim value.
     *
     * @param access     the corresponding access object
     * @param csvParser  parser which is used to parse the file
     * @param nulls      a map which indicates if a certain string value should be seen as a null
     * @param skipHeader indicates if the first record should be skipped or not
     * @param trim       indicates if the values should be String.trim()
     */
    public void open(Access access, com.opencsv.CSVParserBuilder csvParser, List<String> nulls, boolean skipHeader, boolean trim) {
        if (csvParser != null) {
            this.csvParser = csvParser;
        }
        this.nulls = nulls;
        dataTypes = access.getDataTypes();
        this.trim = trim;
        int skipLines = skipHeader ? 1 : 0;

        try (BOMInputStream inputStream = new BOMInputStream(access.getInputStream())) {
            iterator = new CSVReaderBuilder(new InputStreamReader(inputStream, csvCharset))
                    .withCSVParser(this.csvParser.build())
                    .withSkipLines(skipLines)
                    .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                    .build().iterator();

            if (iterator.hasNext()) {
                header = iterator.next();
                checkHeader(header);
            } else {
                //TODO exception
            }
        } catch (SQLException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
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

    /**
     * Checks if @record has a string value which is in the nulls list, if so sets this value to null in the data map.
     *
     * @param record
     * @return
     */
    public CSVSource replaceNulls(CSVSource record) {
        Map<String, String> data = record.getData();
        data.forEach((key, value) -> {
            if (this.nulls.contains(value)) {
                data.put(key, null);
            }
        });
        return record;
    }

    @Override
    public Source next() {
        if (iterator.hasNext()) {
            String[] item = iterator.next();
            // legacy code that should throw empty rows away
            if (item.length == 0 || (item.length == 1 && item[0] == null)) return next();

            if (trim) {
                item = Arrays.stream(item)
                        .map(String::trim)
                        .toArray(String[]::new);
            }
            return replaceNulls(new CSVSource(header, item, dataTypes));
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }
}
