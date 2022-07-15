package be.ugent.idlab.knows.iterator;

import be.ugent.idlab.knows.TestCore;
import be.ugent.idlab.knows.source.CSVSource;
import be.ugent.idlab.knows.source.CSVSourceIterator;
import be.ugent.idlab.knows.source.ExcelSource;
import be.ugent.idlab.knows.source.ExcelSourceIterator;
import org.apache.poi.ss.usermodel.*;
import org.junit.Test;


import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;

public class ExcelTest  extends TestCore {

    //only works for String or a specific type, cant mix because of annoying setCellValue()
    private Row makeRow(String... args){
        Row row = null;
        try {
            Sheet sheet = WorkbookFactory.create(true).createSheet();
            row = sheet.createRow(0);
            for(int i = 0; i < args.length; i++){
                row.createCell(i, STRING).setCellValue(args[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return row;
    }

    @Test
    public void evaluate_0000_excel(){
        try {
            Row header = makeRow("ID", "Name");

            Sheet sheet = WorkbookFactory.create(true).createSheet();
            Row row = sheet.createRow(0);
            row.createCell(0, NUMERIC).setCellValue(10.0);
            row.createCell(1, STRING).setCellValue("Venus");

            compareIterator(new ExcelSourceIterator(makeLocalAccess("/excel/0000.xlsx")),
                    Set.of(new ExcelSource(header, row)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void evaluate_0001_CSV(){
        try {
            Row header = makeRow("name","description","address");

            ExcelSource source1 = new ExcelSource(header, makeRow("Trollekelder","Beer café in the shadows of the St James' church","Bij Sint-Jacobs 17 9000 Gent Belgium"));
            ExcelSource source2 = new ExcelSource(header, makeRow("Hot Club Gent","Live jazz concerts in an intimate setting","Schuddevisstraatje 2 - Groentenmarkt 9000 Gent Belgium"));
            ExcelSource source3 = new ExcelSource(header, makeRow("Jan van Gent","Quirky local pub with a popular summer terrace","Annonciadenstraat 1 9000 Gent Belgium"));

            compareIterator(new ExcelSourceIterator(makeLocalAccess("/excel/0001.xlsx")),
                    Set.of(source1, source2, source3));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
