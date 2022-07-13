package be.ugent.idlab.knows.source;

import be.ugent.idlab.knows.access.Access;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.opencsv.exceptions.CsvException;
import org.apache.commons.io.input.BOMInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CSVSourceFactory implements SourceFactory {

    private static final Logger logger = LoggerFactory.getLogger(CSVSourceFactory.class);

    /**
     * This method returns a CSVParser from a simple access (local/remote CSV file; no CSVW).
     *
     * @param access the used access.
     * @return a CSVParser.
     * @throws IOException
     */
    public Stream<Source> getRecords(Access access) throws IOException, SQLException, ClassNotFoundException {
        try {
            // RDBs fall under this
            try (BOMInputStream inputStream = new BOMInputStream(access.getInputStream());
                 CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                         .withSkipLines(0)
                         .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                         .build();
            ) {
                // TODO this should remove the header out of the stream and thus remove the need to use .skip(1)
                final String[] header = reader.readNext();
                return StreamSupport.stream(reader.spliterator(), false)
                        //.skip(1)
                        // throw away empty records
                        .filter(r -> r.length != 0 && !(r.length == 1 && r[0] == null))
                        .map(record -> new CSVSource(header, record, access.getDataTypes()));
            }
        } catch (IllegalArgumentException | CsvException e) {
            // We still return an empty list of records when a parser is not found.
            // This is to support certain use cases with RDBs where queries might not be valid,
            // but you don't want the RMLMapper to crash.
            logger.debug("Could not parse CSV inputstream", e);
            return Stream.of();
        }
    }
}
