package be.ugent.idlab.knows.iterators;

import be.ugent.idlab.knows.access.Access;
import be.ugent.idlab.knows.source.ExcelSource;
import be.ugent.idlab.knows.source.Source;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;


public class ExcelSourceIterator extends SourceIterator {

    private static final Logger logger = LoggerFactory.getLogger(ExcelSourceIterator.class);
    private java.util.Iterator<org.apache.poi.ss.usermodel.Sheet> workbookIterator;
    private Iterator<Row> iterator;
    private Row header;

    /**
     * Opens the files using the access object and initiates the workbookIterator, iterator and header.
     * @param access the corresponding access object
     */
    public void open(Access access) {
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

        } catch (SQLException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Source next() {
        // has next updates the iterators
        if (iterator.hasNext()){
            return new ExcelSource(header, iterator.next());
        }  else{
            throw new NoSuchElementException();
        }
    }

    /**
     * If the iterator of the current workbook is at its end look if there is a next workbook and initiate the new iterator and header
     * @return true if (new) itererator hasNext()
     */
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
