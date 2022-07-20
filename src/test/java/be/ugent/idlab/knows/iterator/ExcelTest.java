package be.ugent.idlab.knows.iterator;

import be.ugent.idlab.knows.TestCore;
import be.ugent.idlab.knows.iterators.ExcelSourceIterator;
import org.junit.Test;

public class ExcelTest  extends TestCore {

//    //only works for String or a specific type, cant mix because of annoying setCellValue()
//    private Row makeRow(String... args){
//        Row row = null;
//        try {
//            Sheet sheet = WorkbookFactory.create(true).createSheet();
//            row = sheet.createRow(0);
//            for(int i = 0; i < args.length; i++){
//                row.createCell(i, STRING).setCellValue(args[i]);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return row;
//    }

    @Test
    public void evaluate_0000_excel(){
        ExcelSourceIterator excelSourceIterator = new ExcelSourceIterator();
        excelSourceIterator.open(makeLocalAccess("/excel/0000.xlsx"));
        evaluate_0000(excelSourceIterator, true);
    }

    @Test
    public void evaluate_0001_CSV(){
        ExcelSourceIterator excelSourceIterator = new ExcelSourceIterator();
        excelSourceIterator.open(makeLocalAccess("/excel/0001.xlsx"));
        evaluate_0001(excelSourceIterator);

//        Row header = makeRow("name","description","address");
//
//        ExcelSource source1 = new ExcelSource(header,
//                makeRow("Trollekelder","Beer café in the shadows of the St James' church","Bij Sint-Jacobs 17 9000 Gent Belgium"));
//        ExcelSource source2 = new ExcelSource(header,
//                makeRow("Hot Club Gent","Live jazz concerts in an intimate setting","Schuddevisstraatje 2 - Groentenmarkt 9000 Gent Belgium"));
//        ExcelSource source3 = new ExcelSource(header,
//                makeRow("Jan van Gent","Quirky local pub with a popular summer terrace","Annonciadenstraat 1 9000 Gent Belgium"));
//
//        compareIterator(excelSourceIterator, Set.of(source1, source2, source3));
    }
}
