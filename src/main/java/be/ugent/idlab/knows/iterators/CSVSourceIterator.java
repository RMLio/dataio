package be.ugent.idlab.knows.iterators;

import be.ugent.idlab.knows.access.Access;

import be.ugent.idlab.knows.source.CSVSource;
import be.ugent.idlab.knows.source.Source;
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
import java.util.NoSuchElementException;

public class CSVSourceIterator extends SourceIterator {

    private static final Logger logger = LoggerFactory.getLogger(CSVSourceIterator.class);
    private Iterator<String[]> iterator;
    private String[] header;
    private Map<String, String> dataTypes;

    /**
     * Opens the files using the access object and initiates the iterator and header
     * @param access the corresponding access object
     */
    public void open(Access access){
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

    /**
     * Gets the next value of the iterator and creates a source with this value
     * @return
     */
    public Source next(){
        if(iterator.hasNext()){
            return new CSVSource(header, iterator.next(), dataTypes);
        } else {
            throw new NoSuchElementException();
        }
    }

    public boolean hasNext(){
        return iterator.hasNext();
    }
}