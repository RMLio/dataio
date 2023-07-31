package be.ugent.idlab.knows.dataio.streams;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.source.ExcelSource;
import be.ugent.idlab.knows.dataio.source.Source;
import com.github.pjfanning.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ExcelSourceStream implements SourceStream {

    private static final long serialVersionUID = -5883688877961394710L;
    private transient Iterator<Sheet> iterator;
    private transient Workbook wb;
    private transient InputStream in;
    private final Access access;
    private Row header;

    public ExcelSourceStream(Access access) throws IOException, SQLException {
        this.access = access;
        this.bootstrap();
    }

    private void bootstrap() throws SQLException, IOException {
        this.in = this.access.getInputStream();
        this.wb = StreamingReader.builder()
                .open(in);
        this.iterator = this.wb.iterator();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException, SQLException {
        in.defaultReadObject();
        this.bootstrap();
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

    @Override
    public void close() throws IOException {
        this.wb.close();
        this.in.close();
    }
}
