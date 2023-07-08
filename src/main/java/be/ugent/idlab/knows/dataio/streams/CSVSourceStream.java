package be.ugent.idlab.knows.dataio.streams;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.source.CSVSource;
import be.ugent.idlab.knows.dataio.source.Source;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CSVSourceStream implements SourceStream {
    private static final Logger logger = LoggerFactory.getLogger(CSVSourceStream.class.getName());
    private final Access access;
    private final CSVReader reader;
    private final String[] header;

    public CSVSourceStream(Access access) throws SQLException, IOException {
        this.access = access;
        this.reader = new CSVReaderBuilder(new InputStreamReader(access.getInputStream()))
                .withSkipLines(0)
                .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                .build();

        try {
            this.header = reader.readNext();
        } catch (CsvValidationException e) {
            logger.error("Exception thrown during reading the header", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Stream<Source> getStream() {
        return StreamSupport.stream(this.reader.spliterator(), false)
                .map(data -> new CSVSource(header, data, this.access.getDataTypes()));
    }

    @Override
    public void close() throws IOException {
        this.reader.close();
    }
}


