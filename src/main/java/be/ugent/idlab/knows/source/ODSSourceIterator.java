package be.ugent.idlab.knows.source;

import be.ugent.idlab.knows.access.Access;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Iterator;

public class ODSSourceIterator implements SourceIterator {

    private static final Logger logger = LoggerFactory.getLogger(CSVSourceIterator.class);
    private Iterator<Table> tableIterator;
    private Iterator<Row> iterator;
    private Row header;

    public ODSSourceIterator(Access access){
        try (InputStream is = access.getInputStream()) {
            //TODO find way to do this without first loading the full list
            tableIterator = SpreadsheetDocument.loadDocument(is).getTableList().iterator();

            if(tableIterator.hasNext()){
                iterator = tableIterator.next().getRowIterator();
            } else{
                System.out.println("Tis gefucked");
                //TODO exception
            }

            if(iterator.hasNext()){
                header = iterator.next();
            } else{
                if(updateIterators()){
                    header = iterator.next();
                } else{
                    System.out.println("ook gefucked");
                    //TODO exception
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean updateIterators(){
        if(! iterator.hasNext()){
            if(tableIterator.hasNext()){
                iterator = tableIterator.next().getRowIterator();
                if(iterator.hasNext()){
                    header = iterator.next();
                    return true;
                } else {
                    return updateIterators();
                }
            } else {
                // both iterator and workbookIterator don't have a next
                return false;
            }
        }
        return true;
    }

    @Override
    public Source nextSource() {
        // has next updates the iterators
        if (iterator.hasNext()){
            return new ODSSource(header, iterator.next());
        } else {
            // TODO make exception
            return null;
        }
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext() || updateIterators();
    }
}
