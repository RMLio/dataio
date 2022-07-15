package be.ugent.idlab.knows.source;

import be.ugent.idlab.knows.access.Access;
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

public class CSVWSourceIterator implements SourceIterator {

    private static final Logger logger = LoggerFactory.getLogger(CSVSourceIterator.class);

    private com.opencsv.CSVParserBuilder csvParser = new CSVParserBuilder().withIgnoreLeadingWhiteSpace(true);
    private Charset csvCharset = StandardCharsets.UTF_8;

    private List<String> nulls;
    private Iterator<String[]> iterator;
    private String[] header;
    private final Map<String, String> dataTypes;
    private boolean trim;

    public CSVWSourceIterator(Access access, com.opencsv.CSVParserBuilder csvParser, List<String> nulls, boolean skipHeader, boolean trim){
        if(csvParser != null){
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

            if(iterator.hasNext()){
                header =  iterator.next();
            } else {
                //TODO exception
            }
        } catch (SQLException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
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

    @Override
    public Source nextSource() {
        if(iterator.hasNext()){
            String[] item = iterator.next();
            // legacy code that should throw empty rows away
            if (item.length == 0 || (item.length == 1 && item[0] == null)) return nextSource();

            if(trim) {
                item = Arrays.stream(item)
                        .map(String::trim)
                        .toArray(String[]::new);
            }
            return replaceNulls(new CSVSource(header, item, dataTypes));
        }
        return null;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }
}
