package be.ugent.idlab.knows.dataio.iterators;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.csvw.CSVWConfiguration;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.Serial;
import java.sql.SQLException;
import java.util.Collection;

/**
 * An iterator for CSV Sources.
 * Subclass CSVWSourceIterator, as regular CSV is just a special case of CSVW.
 */
public class CSVSourceIterator extends CSVWSourceIterator {
    @Serial
    private static final long serialVersionUID = -352163251763906563L;

    public CSVSourceIterator(Access access) throws SQLException, IOException, ParserConfigurationException, TransformerException {
        super(access, CSVWConfiguration.DEFAULT);
    }

    public CSVSourceIterator(Access access, Collection<String> nulls) throws SQLException, IOException, ParserConfigurationException, TransformerException {
        super(access, CSVWConfiguration.builder().withNulls(nulls).build());
    }
}
