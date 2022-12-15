package be.ugent.idlab.knows.dataio.iterators;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.source.ExcelSource;
import be.ugent.idlab.knows.dataio.source.Source;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ExcelSourceIterator extends SourceIterator {
    private Iterator<ExcelSource> iterator;

    /**
     * Opens the files using the access object and initiates the workbookIterator, iterator and header.
     *
     * @param access the corresponding access object
     */
    public void open(Access access) {
        XSSFWorkbook wb;
        try {
            wb = new XSSFWorkbook(access.getInputStream());
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }

        List<ExcelSource> sources = new ArrayList<>();
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            XSSFSheet sheet = wb.getSheetAt(i);

            Iterator<Row> iterator = sheet.iterator();

            // add the sources only if they're available
            if (iterator.hasNext()) {
                Row header = iterator.next();

                iterator.forEachRemaining(row -> sources.add(new ExcelSource(header, row)));
            }
        }

        this.iterator = sources.iterator();
    }

    @Override
    public Source next() {
        return this.iterator.next();
    }

    @Override
    public boolean hasNext() {
        return this.iterator.hasNext();
    }
}
