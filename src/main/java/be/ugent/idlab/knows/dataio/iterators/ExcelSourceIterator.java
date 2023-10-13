package be.ugent.idlab.knows.dataio.iterators;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.record.ExcelRecord;
import be.ugent.idlab.knows.dataio.record.Record;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelSourceIterator extends SourceIterator {
    private static final long serialVersionUID = 5223150147849184514L;
    private final Access access;
    private transient Iterator<ExcelRecord> iterator;
    private transient XSSFWorkbook wb;

    public ExcelSourceIterator(Access access) throws IOException, SQLException {
        this.access = access;
        this.boostrap();
    }

    /**
     * Instantiates transient fields. This code needs to be run both at construction time and after deserialization
     *
     * @throws IOException  can be thrown due to the consumption of the input stream. Same for SQLException.
     * @throws SQLException
     */
    private void boostrap() throws SQLException, IOException {
        this.wb = new XSSFWorkbook(this.access.getInputStream());

        List<ExcelRecord> sources = new ArrayList<>();
        for (int i = 0; i < this.wb.getNumberOfSheets(); i++) {
            XSSFSheet sheet = this.wb.getSheetAt(i);

            Iterator<Row> iterator = sheet.iterator();

            // add the sources only if they're available
            if (iterator.hasNext()) {
                Row header = iterator.next();

                iterator.forEachRemaining(row -> sources.add(new ExcelRecord(header, row)));
            }
        }

        this.iterator = sources.iterator();
    }

    private void readObject(ObjectInputStream inputStream) throws SQLException, IOException, ClassNotFoundException {
        inputStream.defaultReadObject();
        boostrap();
    }

    @Override
    public Record next() {
        return this.iterator.next();
    }

    @Override
    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    @Override
    public void close() throws IOException {
        this.wb.close();
    }
}
