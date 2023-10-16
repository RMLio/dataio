package be.ugent.idlab.knows.dataio.streams;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.record.ExcelRecord;
import be.ugent.idlab.knows.dataio.record.Record;
import com.github.pjfanning.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Stream of Excel records.
 */
public class ExcelSourceStream implements SourceStream {
    private static final long serialVersionUID = -5883688877961394710L;
    private final Access access;
    private transient Iterator<Sheet> iterator;
    private transient Workbook wb;
    private transient InputStream in;
    private Row header;

    public ExcelSourceStream(Access access) throws Exception {
        this.access = access;
        this.bootstrap();
    }

    private void bootstrap() throws Exception {
        this.in = this.access.getInputStream();

        this.wb = StreamingReader.builder()
                .open(in);
        this.iterator = this.wb.iterator();
    }

    private void readObject(ObjectInputStream in) throws Exception {
        in.defaultReadObject();
        this.bootstrap();
    }

    /**
     * Provides a Stream object to be consumed
     *
     * @return a Stream object
     */
    @Override
    public Stream<Record> getStream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this.iterator, Spliterator.ORDERED), true)
                .peek(sheet -> this.header = sheet.iterator().next()) // grab the header for current sheet
                .flatMap(this::getRowStreamFromSheet)
                .skip(1) // skip the header
                .map(row -> new ExcelRecord(header, row));
    }

    private Stream<Row> getRowStreamFromSheet(Sheet sheet) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(sheet.rowIterator(), Spliterator.ORDERED), true);
    }

    @Override
    public void close() throws IOException {
        this.wb.close();
        this.in.close();
    }
}
