package be.ugent.idlab.knows.source;

import be.ugent.idlab.knows.access.Access;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.SpreadsheetDocument;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

public class ODSSourceFactory implements SourceFactory {

    /**
     * Get Records for ODT file format.
     * @param access
     * @return
     * @throws IOException
     */
    public Stream<Source> getRecords(Access access) throws Exception {
        Stream<Source> output = Stream.of();

        try (InputStream is = access.getInputStream()) {
            Document document = SpreadsheetDocument.loadDocument(is);
            for (org.odftoolkit.simple.table.Table table : document.getTableList()) {
                org.odftoolkit.simple.table.Row header = table.getRowByIndex(0);
                // TODO find way to do this without first going to list
                Stream<Source> temp_stream = table.getRowList().stream()
                        .skip(1).map(row -> new ODSSource(header, row));
                output = Stream.concat(output, temp_stream);
            }
        }
        return output;
    }
}
