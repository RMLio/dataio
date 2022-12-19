package be.ugent.idlab.knows.dataio.flow.observables;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.flow.base.SourceObservable;
import be.ugent.idlab.knows.dataio.source.XMLSource;
import be.ugent.idlab.knows.dataio.streams.XMLSourceStream;
import io.reactivex.rxjava3.core.Observable;

public class XMLObservable extends SourceObservable<XMLSource> {
    public XMLObservable(Access access, String xpath) {
        super(access, () -> new XMLSourceStream(xpath));
    }
}
