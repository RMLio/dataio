package be.ugent.idlab.knows.iterators;

import be.ugent.idlab.knows.access.Access;
import be.ugent.idlab.knows.source.ODSSource;
import be.ugent.idlab.knows.source.Source;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ODSSourceIterator extends SourceIterator {

    private static final Logger logger = LoggerFactory.getLogger(CSVSourceIterator.class);
    private Iterator<Table> tableIterator;
    private Iterator<Row> iterator;
    private Row header;

    /**
     * This function partly loads the full file in at once (to read the tables).
     * Opens the files using the access object and initiates the tableIterator, iterator and header.
     * @param access the corresponding access object
     */
    public void open(Access access){
        try (InputStream is = access.getInputStream()) {
            //TODO find way to do this without first loading the full list
            tableIterator = SpreadsheetDocument.loadDocument(is).getTableList().iterator();

            if(tableIterator.hasNext()){
                iterator = tableIterator.next().getRowIterator();
            } else{
                //TODO exception
            }

            if(iterator.hasNext()){
                header = iterator.next();
            } else{
                if(updateIterators()){
                    header = iterator.next();
                } else{
                    //TODO exception
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * If the iterator of the current table is at its end, look if there is a next table and initiate the new iterator and header
     * @return true if (new) itererator hasNext()
     */
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
    public Source next() {
        // has next updates the iterators
        if (iterator.hasNext()){
            return new ODSSource(header, iterator.next());
        }  else{
            throw new NoSuchElementException();
        }
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext() || updateIterators();
    }
}
