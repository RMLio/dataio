package be.ugent.idlab.knows.dataio.flow.observables;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.flow.base.SourceObservable;
import be.ugent.idlab.knows.dataio.source.CSVSource;
import be.ugent.idlab.knows.dataio.streams.CSVWSourceStream;
import com.opencsv.CSVParserBuilder;

import java.util.List;

public class CSVWObservable extends SourceObservable<CSVSource> {

    public CSVWObservable(Access access, CSVParserBuilder parser, List<String> nulls, boolean skipHeader, boolean trim) {
        super(access, () -> new CSVWSourceStream(parser, nulls, skipHeader, trim));
    }
}
