package be.ugent.idlab.knows.source;

import be.ugent.idlab.knows.access.Access;

import java.io.IOException;
import java.sql.SQLException;
import java.util.stream.Stream;

public interface SourceFactory {
    Stream<Source> getRecords(Access access) throws IOException, SQLException, ClassNotFoundException, Exception;
}
