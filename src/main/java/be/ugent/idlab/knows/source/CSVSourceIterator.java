package be.ugent.idlab.knows.source;

import be.ugent.idlab.knows.access.Access;

import com.opencsv.CSVReaderBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import org.apache.commons.io.input.BOMInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

public class CSVSourceIterator implements SourceIterator {

    private static final Logger logger = LoggerFactory.getLogger(CSVSourceIterator.class);
    private Iterator<String[]> iterator;
    private String[] header;
    private final Map<String, String> dataTypes;

    public CSVSourceIterator(Access access){
        dataTypes = access.getDataTypes();
        try (BOMInputStream inputStream = new BOMInputStream(access.getInputStream())){
            iterator = new CSVReaderBuilder(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .withSkipLines(0)
                    .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                    .build().iterator();
            header = iterator.next();
        } catch (IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Source nextSource(){
        if(iterator.hasNext()){
            return new CSVSource(header, iterator.next(), dataTypes);
        }
        return null;
    }

    public boolean hasNext(){
        return iterator.hasNext();
    }
}
