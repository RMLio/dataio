package be.ugent.idlab.knows.iterator;

import be.ugent.idlab.knows.TestCore;
import be.ugent.idlab.knows.iterators.CSVSourceIterator;
import be.ugent.idlab.knows.iterators.ODSSourceIterator;
import org.junit.Test;


public class ODSTest extends TestCore {

//    private Row makeRow(String... args) {
//        Row row = null;
//        try {
//            Document document = TextDocument.newTextDocument();
//            Table table = Table.newTable(document);
//            row = table.appendRow();
//
//            for(int i = 0; i < args.length; i++){
//                row.getCellByIndex(i).setStringValue(args[i]);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return row;
//
//    }


    @Test
    public void evaluate_0000_ods(){
        ODSSourceIterator odsSourceIterator = new ODSSourceIterator();
        odsSourceIterator.open(makeLocalAccess("/ods/0000.ods"));
        evaluate_0000(odsSourceIterator, true);
//        try {
//            ODSSourceIterator odsSourceIterator = new ODSSourceIterator();
//            odsSourceIterator.open(makeLocalAccess("/ods/0000.ods"));
//            TextDocument document = TextDocument.newTextDocument();
//            Table table = Table.newTable(document);
//            Row header = makeRow( "ID", "Name");
//
//            Row row = table.appendRow();
//            row.getCellByIndex(0).setDoubleValue(10.0);
//            row.getCellByIndex(1).setStringValue("Venus");
//
//            compareIterator(odsSourceIterator, Set.of(new ODSSource(header, row)));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Test
    public void evaluate_0001_ods(){
        ODSSourceIterator odsSourceIterator = new ODSSourceIterator();
        odsSourceIterator.open(makeLocalAccess("/ods/0001.ods"));
        evaluate_0001(odsSourceIterator);
//        try {
//            ODSSourceIterator odsSourceIterator = new ODSSourceIterator();
//            odsSourceIterator.open(makeLocalAccess("/ods/0001.ods"));
//
//            Row header = makeRow("name","description","address");
//
//            ODSSource source1 = new ODSSource(header, makeRow( "Trollekelder","Beer café in the shadows of the St James' church","Bij Sint-Jacobs 17 9000 Gent Belgium"));
//            ODSSource source2 = new ODSSource(header, makeRow("Hot Club Gent","Live jazz concerts in an intimate setting","Schuddevisstraatje 2 - Groentenmarkt 9000 Gent Belgium"));
//            ODSSource source3 = new ODSSource(header, makeRow("Jan van Gent","Quirky local pub with a popular summer terrace","Annonciadenstraat 1 9000 Gent Belgium"));
//
//            compareIterator(odsSourceIterator, Set.of(source1, source2, source3));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Test
    public void evaluate_1001_header_col_missing_CSV(){
        ODSSourceIterator odsSourceIterator = new ODSSourceIterator();
        odsSourceIterator.open(makeLocalAccess("/ods/1001_header_col_missing.ods"));
        //TODO should fail, check if it does
    }

    @Test
    public void evaluate_1001_header_long_CSV(){
        ODSSourceIterator odsSourceIterator = new ODSSourceIterator();
        odsSourceIterator.open(makeLocalAccess("/ods/1001_header_long.ods"));
        evaluate_1001_header_long(odsSourceIterator);
    }

    @Test
    public void evaluate_1001_header_short_CSV(){
        ODSSourceIterator odsSourceIterator = new ODSSourceIterator();
        odsSourceIterator.open(makeLocalAccess("/ods/1001_header_short.ods"));
        evaluate_1001_header_short(odsSourceIterator);
    }
}
