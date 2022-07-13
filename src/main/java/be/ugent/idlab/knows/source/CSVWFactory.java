package be.ugent.idlab.knows.source;

import be.ugent.idlab.knows.access.Access;
import be.ugent.rml.records.CSVRecord;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import org.apache.commons.io.input.BOMInputStream;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CSVWFactory {

    private List<String> nulls = new ArrayList<>();

    public Stream<Source> getRecords(Access access, com.opencsv.CSVParserBuilder csvParser, String commentPrefix, List<String> nulls, boolean skipHeader, boolean trim) throws SQLException, IOException, ClassNotFoundException {
        int skipLines = skipHeader ? 1 : 0;
        this.nulls = nulls;
        try (BOMInputStream inputStream = new BOMInputStream(access.getInputStream());
            CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .withCSVParser(csvParser.build())
                    .withSkipLines(skipLines)
                    .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                    .build();
            ){
            List<String[]> records = StreamSupport.stream(reader.spliterator(), false)
                    .filter(row -> !row[0].startsWith(commentPrefix))
                    .toList();

            String[] header = records.get(0);

            Stream<String[]> readRecords = records
                    .stream()
                    .skip(1)
                    // throw away empty records
                    .filter(r -> r.length != 0 && !(r.length == 1 && r[0] == null));
            if (trim) { // trim each record value
                readRecords = readRecords.map(r -> Arrays.stream(r)
                        .map(String::trim)
                        .toArray(String[]::new));
            }
            return readRecords
                    .map(record -> new CSVSource(header, record, access.getDataTypes()))
                    .map(this::replaceNulls);
        }

    }

    public CSVSource replaceNulls(CSVSource record){
        Map<String, String> data = record.getData();
        data.forEach((key, value) -> {
            if (this.nulls.contains(value)) {
                data.put(key, null);
            }
        });
        return record;
    }
}
