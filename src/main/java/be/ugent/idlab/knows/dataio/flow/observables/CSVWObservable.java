package be.ugent.idlab.knows.dataio.flow.observables;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.flow.base.SourceObservable;
import be.ugent.idlab.knows.dataio.iterators.csvw.CSVWConfiguration;
import be.ugent.idlab.knows.dataio.record.CSVRecord;
import be.ugent.idlab.knows.dataio.streams.CSVWSourceStream;

public class CSVWObservable extends SourceObservable<CSVRecord> {

    private static final long serialVersionUID = -612391630872334478L;

    public CSVWObservable(Access access, CSVWConfiguration config) {
        super(access, () -> new CSVWSourceStream(access, config));
    }
}
