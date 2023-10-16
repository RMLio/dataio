package be.ugent.idlab.knows.dataio.iterators;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.csvw.CSVWConfiguration;

/**
 * An iterator for CSV Sources.
 * Subclass CSVWSourceIterator, as regular CSV is just a special case of CSVW.
 */
public class CSVSourceIterator extends CSVWSourceIterator {
    private static final long serialVersionUID = -352163251763906563L;

    public CSVSourceIterator(Access access) throws Exception {
        super(access, CSVWConfiguration.DEFAULT);
    }
}
