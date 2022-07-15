package be.ugent.idlab.knows.iterator;

import be.ugent.idlab.knows.TestCore;
import be.ugent.idlab.knows.source.ExcelSource;
import be.ugent.idlab.knows.source.ExcelSourceIterator;
import be.ugent.idlab.knows.source.ODSSource;
import be.ugent.idlab.knows.source.ODSSourceIterator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Test;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;

import java.io.IOException;
import java.util.Set;

import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;

public class ODSTest extends TestCore {

    private Row makeRow(String... args) {
        Row row = null;
        try {
            Document document = TextDocument.newTextDocument();
            Table table = Table.newTable(document);
            row = table.appendRow();

            for(int i = 0; i < args.length; i++){
                row.getCellByIndex(i).setStringValue(args[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return row;

    }


    @Test
    public void evaluate_0000_ods(){
        try {
            TextDocument document = TextDocument.newTextDocument();
            Table table = Table.newTable(document);
            Row header = makeRow( "ID", "Name");

            Row row = table.appendRow();
            row.getCellByIndex(0).setDoubleValue(10.0);
            row.getCellByIndex(1).setStringValue("Venus");

            compareIterator(new ODSSourceIterator(makeLocalAccess("/ods/0000.ods")),
                    Set.of(new ODSSource(header, row)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void evaluate_0001_ods(){

        try {
            new ODSSourceIterator(makeLocalAccess("/ods/0001.ods"));
            TextDocument document = TextDocument.newTextDocument();
            Table table = Table.newTable(document);
            Row header = makeRow("name","description","address");

            ODSSource source1 = new ODSSource(header, makeRow( "Trollekelder","Beer café in the shadows of the St James' church","Bij Sint-Jacobs 17 9000 Gent Belgium"));
            ODSSource source2 = new ODSSource(header, makeRow("Hot Club Gent","Live jazz concerts in an intimate setting","Schuddevisstraatje 2 - Groentenmarkt 9000 Gent Belgium"));
            ODSSource source3 = new ODSSource(header, makeRow("Jan van Gent","Quirky local pub with a popular summer terrace","Annonciadenstraat 1 9000 Gent Belgium"));

            compareIterator(new ODSSourceIterator(makeLocalAccess("/ods/0001.ods")),
                    Set.of(source1, source2, source3));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
