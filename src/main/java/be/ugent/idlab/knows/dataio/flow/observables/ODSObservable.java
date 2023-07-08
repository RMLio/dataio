package be.ugent.idlab.knows.dataio.flow.observables;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.flow.base.SourceObservable;
import be.ugent.idlab.knows.dataio.source.ODSSource;
import be.ugent.idlab.knows.dataio.streams.ODSSourceStream;
import io.reactivex.rxjava3.core.Observable;

public class ODSObservable extends SourceObservable<ODSSource> {
    public ODSObservable(Access access) {
        super(access, () -> new ODSSourceStream(access));
    }
}
