package be.ugent.idlab.knows.dataio.flow.observables;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.flow.base.SourceObservable;
import be.ugent.idlab.knows.dataio.record.ODSRecord;
import be.ugent.idlab.knows.dataio.streams.ODSSourceStream;

public class ODSObservable extends SourceObservable<ODSRecord> {
    private static final long serialVersionUID = -6021591181499655401L;

    public ODSObservable(Access access) {
        super(access, () -> new ODSSourceStream(access));
    }
}
