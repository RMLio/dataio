package be.ugent.idlab.knows.dataio.streams;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.source.CSVSource;
import be.ugent.idlab.knows.dataio.source.Source;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CSVWSourceStream implements SourceStream {

    private final List<String> nulls;
    private final int skipLinesCount;
    private final boolean trim;
    private final CSVParserBuilder parser;
    private CSVReader reader;
    private String[] header;
    private Access access;


    public CSVWSourceStream(CSVParserBuilder parser, List<String> nulls, boolean skipHeader, boolean trim) {
        this.parser = parser;
        this.nulls = nulls;
        this.skipLinesCount = skipHeader ? 1 : 0;
        this.trim = trim;
    }

    @Override
    public void open(Access access) throws SQLException, IOException {
        this.access = access;

        CSVReaderBuilder builder = new CSVReaderBuilder(new InputStreamReader(access.getInputStream()))
                .withSkipLines(skipLinesCount)
                .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS);

        if (this.parser != null) {
            builder = builder.withCSVParser(this.parser.build());
        }

        this.reader = builder.build();

        try {
            this.header = reader.readNext();
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Stream<Source> getStream() {
        return StreamSupport
                .stream(this.reader.spliterator(), false)
                .map(data -> {
                    if (this.trim) {
                        data = Arrays.stream(data)
                                .map(String::trim)
                                .toArray(String[]::new);
                    }

                    return replaceNulls(new CSVSource(header, data, access.getDataTypes()));
                });
    }

    private CSVSource replaceNulls(CSVSource record) {
        Map<String, String> data = record.getData();
        data.forEach((key, value) -> {
            if (this.nulls.contains(value)) {
                data.put(key, null);
            }
        });
        return record;
    }
}
