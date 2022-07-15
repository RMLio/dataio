package be.ugent.idlab.knows.source;

import be.ugent.idlab.knows.access.Access;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Iterator;


public class ExcelSourceIterator implements SourceIterator {

    private static final Logger logger = LoggerFactory.getLogger(CSVSourceIterator.class);
    private java.util.Iterator<org.apache.poi.ss.usermodel.Sheet> workbookIterator;
    private Iterator<Row> iterator;
    private Row header;

    public ExcelSourceIterator(Access access) throws IOException {
        try (InputStream is = access.getInputStream()){
            workbookIterator = new XSSFWorkbook(is).iterator();
            if(workbookIterator.hasNext()){
                iterator = workbookIterator.next().iterator();
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

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Source nextSource() {
        // has next updates the iterators
        if (iterator.hasNext()){
            return new ExcelSource(header, iterator.next());
        } else {
            // TODO make exception
            return null;
        }
    }

    private boolean updateIterators(){
        if(! iterator.hasNext()){
            if(workbookIterator.hasNext()){
                iterator = workbookIterator.next().iterator();
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
    public boolean hasNext() {
        return iterator.hasNext() || updateIterators();
    }
}
