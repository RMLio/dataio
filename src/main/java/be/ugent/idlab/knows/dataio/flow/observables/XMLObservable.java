package be.ugent.idlab.knows.dataio.flow.observables;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.flow.base.SourceObservable;
import be.ugent.idlab.knows.dataio.record.XMLRecord;
import be.ugent.idlab.knows.dataio.streams.XMLSourceStream;

public class XMLObservable extends SourceObservable<XMLRecord> {
    private static final long serialVersionUID = 6893733526728143212L;

    public XMLObservable(Access access, String xpath) {
        super(access, () -> new XMLSourceStream(access, xpath));
    }
}
