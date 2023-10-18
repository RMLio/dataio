package be.ugent.idlab.knows.dataio.iterators;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.record.ExcelRecord;
import be.ugent.idlab.knows.dataio.record.Record;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelSourceIterator extends SourceIterator {
    private static final long serialVersionUID = 5223150147849184514L;
    private final Access access;
    private transient Iterator<ExcelRecord> iterator;

    public ExcelSourceIterator(Access access) throws Exception {
        this.access = access;
        this.boostrap();
    }

    /**
     * Instantiates transient fields. This code needs to be run both at construction time and after deserialization
     */
    private void boostrap() throws Exception {
        List<ExcelRecord> sources = new ArrayList<>();
        try (InputStream in = access.getInputStream();
             Workbook wb = new XSSFWorkbook(in)) {
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                Sheet sheet = wb.getSheetAt(i);

                Iterator<Row> iterator = sheet.iterator();

                // add the sources only if they're available
                if (iterator.hasNext()) {
                    Row header = iterator.next();

                    iterator.forEachRemaining(row -> sources.add(new ExcelRecord(header, row)));
                }
            }
        }
        this.iterator = sources.iterator();
    }

    private void readObject(ObjectInputStream inputStream) throws Exception {
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
        // do nothing
    }
}
