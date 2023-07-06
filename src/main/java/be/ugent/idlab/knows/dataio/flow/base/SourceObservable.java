package be.ugent.idlab.knows.dataio.flow.base;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.source.Source;
import be.ugent.idlab.knows.dataio.streams.SourceStream;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Callable;

/**
 * Implementation of RxJava's Observables for the sources we support. Relies on SourceStream to produce values for the subscribers.
 * @param <T> parameter subclassing Source
 */
public abstract class SourceObservable<T extends Source> extends Observable<T> {
    protected Access access;
    protected SourceStream stream;

    public SourceObservable(Access access, Callable<SourceStream> stream) {
        this.access = access;
        try {
            this.stream = stream.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void subscribeActual(@NonNull Observer<? super T> observer) {
        stream.getStream().forEach(e -> observer.onNext((T) e));
        observer.onComplete();
    }
}
