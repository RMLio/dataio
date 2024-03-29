package be.ugent.idlab.knows.dataio.flow.observables;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.flow.base.SourceObservable;
import be.ugent.idlab.knows.dataio.record.JSONRecord;
import be.ugent.idlab.knows.dataio.streams.JSONSourceStream;

public class JSONObservable extends SourceObservable<JSONRecord> {
    private static final long serialVersionUID = 7962523493960092150L;

    public JSONObservable(Access access, String jsonPath) {
        super(access, () -> new JSONSourceStream(access, jsonPath));
    }
}
