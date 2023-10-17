package be.ugent.idlab.knows.dataio.flow.observables;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.flow.base.SourceObservable;
import be.ugent.idlab.knows.dataio.record.CSVRecord;
import be.ugent.idlab.knows.dataio.streams.CSVSourceStream;

public class CSVObservable extends SourceObservable<CSVRecord> {
    private static final long serialVersionUID = -3401269845007892988L;

    public CSVObservable(Access access) {
        super(access, () -> new CSVSourceStream(access));
    }
}
