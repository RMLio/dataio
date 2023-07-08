package be.ugent.idlab.knows.dataio.flow.observables;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.flow.base.SourceObservable;
import be.ugent.idlab.knows.dataio.source.CSVSource;
import be.ugent.idlab.knows.dataio.streams.CSVSourceStream;

public class CSVObservable extends SourceObservable<CSVSource> {
    public CSVObservable(Access access) {
        super(access, () -> new CSVSourceStream(access));
    }
}
