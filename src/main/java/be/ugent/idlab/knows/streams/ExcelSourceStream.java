package be.ugent.idlab.knows.streams;

import be.ugent.idlab.knows.access.Access;
import be.ugent.idlab.knows.access.LocalFileAccess;
import be.ugent.idlab.knows.source.ExcelSource;
import be.ugent.idlab.knows.source.Source;
import com.github.pjfanning.xlsx.StreamingReader;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ExcelSourceStream implements SourceStream {

    private Iterator<Sheet> iterator;
    private Row header;

    /**
     * Opens the source and prepares for streaming
     *
     * @param access access to the file
     */
    @Override
    public void open(Access access) throws SQLException, IOException {
        InputStream is;
        if(access instanceof LocalFileAccess) { // local Excel files are not read byte-wise
            is = new FileInputStream(access.getAccessPath());
        } else {
            is = access.getInputStream();
        }

        this.iterator = StreamingReader.builder()
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(is)
                .iterator();
    }

    /**
     * Provides a Stream object to be consumed
     *
     * @return a Stream object
     */
    @Override
    public Stream<Source> getStream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this.iterator, Spliterator.ORDERED), true)
                .peek(sheet -> this.header = sheet.iterator().next()) // grab the header for current sheet
                .flatMap(this::getRowStreamFromSheet)
                .skip(1) // skip the header
                .map(row -> new ExcelSource(header, row));
    }

    private Stream<Row> getRowStreamFromSheet(Sheet sheet) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(sheet.rowIterator(), Spliterator.ORDERED), true);
    }
}
