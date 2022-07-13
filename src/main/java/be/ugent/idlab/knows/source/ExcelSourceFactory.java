package be.ugent.idlab.knows.source;

import be.ugent.idlab.knows.access.Access;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ExcelSourceFactory implements SourceFactory {
    /**
     * Get Records for Excel file format.
     * @param access
     * @return
     * @throws IOException
     */
    public Stream<Source> getRecords(Access access) throws IOException, SQLException, ClassNotFoundException {
        Stream<Source> output = Stream.of();
        try (InputStream is = access.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            for (Sheet datatypeSheet : workbook) {
                Row header = datatypeSheet.getRow(0);
                Stream<Source> temp_stream = StreamSupport.stream(datatypeSheet.spliterator(), false)
                        .skip(1).map(row -> new ExcelSource(header, row));
                output = Stream.concat(output, temp_stream);
            }
        }
        return output;
    }
}
