package be.ugent.idlab.knows.dataio.flow.observables;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.flow.base.SourceObservable;
import be.ugent.idlab.knows.dataio.iterators.csvw.CSVWConfiguration;
import be.ugent.idlab.knows.dataio.source.CSVSource;
import be.ugent.idlab.knows.dataio.streams.CSVWSourceStream;

public class CSVWObservable extends SourceObservable<CSVSource> {

    public CSVWObservable(Access access, CSVWConfiguration config) {
        super(access, () -> new CSVWSourceStream(access, config));
    }
}
