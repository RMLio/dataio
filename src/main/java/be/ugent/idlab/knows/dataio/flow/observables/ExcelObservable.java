package be.ugent.idlab.knows.dataio.flow.observables;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.flow.base.SourceObservable;
import be.ugent.idlab.knows.dataio.source.ExcelSource;
import be.ugent.idlab.knows.dataio.streams.ExcelSourceStream;

public class ExcelObservable extends SourceObservable<ExcelSource> {
    public ExcelObservable(Access access) {
        super(access, () -> new ExcelSourceStream(access));
    }
}
