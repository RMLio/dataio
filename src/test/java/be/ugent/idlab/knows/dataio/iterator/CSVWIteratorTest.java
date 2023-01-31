package be.ugent.idlab.knows.dataio.iterator;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.cores.TestCore;
import be.ugent.idlab.knows.dataio.iterators.CSVWSourceIterator;
import be.ugent.idlab.knows.dataio.iterators.csvw.CSVWConfiguration;
import be.ugent.idlab.knows.dataio.source.CSVSource;
import be.ugent.idlab.knows.dataio.source.Source;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static org.junit.Assert.*;

public class CSVWIteratorTest extends TestCore {
    public void defaultOpen(CSVWSourceIterator iterator, String inputFile) throws SQLException, IOException {
        CSVWConfiguration config = CSVWConfiguration.DEFAULT;
        Access access = new LocalFileAccess(inputFile, "src/test/resources", "csvw");
        iterator.open(access, config);
    }

    @Test
    public void evaluate_0000_trim_true() throws SQLException, IOException {
        CSVWSourceIterator csvwSourceIterator = new CSVWSourceIterator();
        CSVWConfiguration config = CSVWConfiguration.builder().withTrim("true").build();
        csvwSourceIterator.open(makeLocalAccess("/csvw/0000_trim.csv", "", "csvw", "utf-8"), config);
        assertTrue(evaluate_0000(csvwSourceIterator));
    }

    @Test
    public void evaluate_0000_trim_false() throws SQLException, IOException {
        CSVWSourceIterator iterator = new CSVWSourceIterator();
        CSVWConfiguration config = CSVWConfiguration.builder().withTrim(false).build();
        Access access = new LocalFileAccess("", "src/test/resources/csvw/0000_trim.csv", "csv");
        iterator.open(access, config);

        CSVSource expected = new CSVSource(new String[]{"ID", "Name"}, new String[]{"  10  ", "Venus"}, access.getDataTypes());

        assertTrue(iterator.hasNext());
        assertEquals(expected, iterator.next());
    }

    @Test
    public void evaluate_0000_trim_start() throws SQLException, IOException {
        CSVWSourceIterator iterator = new CSVWSourceIterator();
        CSVWConfiguration config = CSVWConfiguration.builder().withTrim("start").build();
        Access access = new LocalFileAccess("", "src/test/resources/csvw/0000_trim.csv", "csv");
        iterator.open(access, config);

        CSVSource expected = new CSVSource(new String[]{"ID", "Name"}, new String[]{"10  ", "Venus"}, access.getDataTypes());

        assertTrue(iterator.hasNext());
        assertEquals(expected, iterator.next());
    }

    @Test
    public void evaluate_0000_trim_end() throws SQLException, IOException {
        CSVWSourceIterator iterator = new CSVWSourceIterator();
        CSVWConfiguration config = CSVWConfiguration.builder().withTrim("end").build();
        Access access = new LocalFileAccess("", "src/test/resources/csvw/0000_trim.csv", "csv");
        iterator.open(access, config);

        CSVSource expected = new CSVSource(new String[]{"ID", "Name"}, new String[]{"  10", "Venus"}, access.getDataTypes());
        assertTrue(iterator.hasNext());
        assertEquals(expected, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void evaluate_0000_trim_bogus() throws SQLException, IOException {
        CSVWSourceIterator iterator = new CSVWSourceIterator();
        CSVWConfiguration config = CSVWConfiguration.builder().withTrim("bogus").build();
        Access access = new LocalFileAccess("", "src/test/resources/csvw/0000_trim.csv", "csv");
        iterator.open(access, config);

        assertThrows(IllegalArgumentException.class, iterator::next);
    }

    @Test
    public void evaluate_1000_nulls() throws SQLException, IOException {
        CSVWSourceIterator csvwSourceIterator = new CSVWSourceIterator();
        Access access = makeLocalAccess("/csvw/1000_nulls.csv", "", "csvw", "utf-8");
        CSVWConfiguration config = CSVWConfiguration.builder().withNulls(List.of("NULL")).build();
        csvwSourceIterator.open(access, config);

        String[] header = new String[]{"ID", "Name"};

        CSVSource source1 = new CSVSource(header, new String[]{"10", "Venus"}, access.getDataTypes());
        CSVSource source2 = new CSVSource(header, new String[]{"12", "Serena"}, access.getDataTypes());
        CSVSource source3 = new CSVSource(header, new String[]{"13", "null"}, access.getDataTypes());

        String[] array = new String[]{"11", null};
        CSVSource source_null = new CSVSource(header, array, access.getDataTypes());

        assertTrue(compareIterator(csvwSourceIterator, Set.of(source1, source2, source3, source_null)));
    }

    @Test
    public void evaluate_1001_header_col_missing_CSVW() throws SQLException, IOException {
        CSVWSourceIterator csvwSourceIterator = new CSVWSourceIterator();
        defaultOpen(csvwSourceIterator, "csv/1001_header_col_missing.csv");
        //TODO should fail, check if it does
    }

    @Test
    public void evaluate_1001_header_long_CSVW() throws SQLException, IOException {
        CSVWSourceIterator csvwSourceIterator = new CSVWSourceIterator();
        defaultOpen(csvwSourceIterator, "csv/1001_header_long.csv");
        assertTrue(evaluate_1001_header_long(csvwSourceIterator));
    }

    @Test
    public void evaluate_1001_header_short_CSVW() throws SQLException, IOException {
        CSVWSourceIterator csvwSourceIterator = new CSVWSourceIterator();
        defaultOpen(csvwSourceIterator, "csv/1001_header_short.csv");
        assertTrue(evaluate_1001_header_short(csvwSourceIterator));
    }

    @Test
    public void evaluate_0000_CSVW() throws SQLException, IOException {
        CSVWSourceIterator csvwSourceIterator = new CSVWSourceIterator();
        defaultOpen(csvwSourceIterator, "csv/0000.csv");
        assertTrue(evaluate_0000(csvwSourceIterator));
    }

    @Test
    public void evaluate_0001_CSVW() throws SQLException, IOException {
        CSVWSourceIterator csvwSourceIterator = new CSVWSourceIterator();
        defaultOpen(csvwSourceIterator, "csv/0001.csv");
        assertTrue(evaluate_0001(csvwSourceIterator));
    }

    @Test
    public void evaluate_0002_CSVW() throws SQLException, IOException {
        CSVWSourceIterator iterator = new CSVWSourceIterator();
        defaultOpen(iterator, "csv/0002_BOM.csv");
        assertTrue(evaluate_0002_BOM(iterator));
    }

    /// Mapper tests

    private void runMapperTest(Access access, CSVWConfiguration config) throws SQLException, IOException {
        CSVWSourceIterator iterator = new CSVWSourceIterator();
        iterator.open(access, config);

        CSVSource expected = new CSVSource(new String[]{"ID", "Name"}, new String[]{"10", "Venus"}, access.getDataTypes());

        assertTrue(iterator.hasNext());
        assertEquals(expected, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void test_00002a_comment_prefix() throws SQLException, IOException {
        Access access = new LocalFileAccess("", "src/test/resources/csvw/mapper/comments.csv", "csv");
        CSVWConfiguration config = CSVWConfiguration.DEFAULT;

        runMapperTest(access, config);
    }

    @Test
    public void test_00002a_delimiter() throws SQLException, IOException {
        Access access = new LocalFileAccess("", "src/test/resources/csvw/mapper/delimiter.csv", "csv");
        CSVWConfiguration config = CSVWConfiguration.builder()
                .withDelimiter(';')
                .build();

        runMapperTest(access, config);
    }

    @Test
    public void test_00002a_encoding() throws SQLException, IOException {
        Access access = new LocalFileAccess("", "src/test/resources/csvw/mapper/encoding.csv", "csv", "utf-16be");
        CSVWConfiguration config = CSVWConfiguration.builder().withEncoding(StandardCharsets.UTF_16).build();

        runMapperTest(access, config);
    }

    @Test
    public void test_00002a_tabs() throws SQLException, IOException {
        Access access = new LocalFileAccess("", "src/test/resources/csvw/mapper/tabs.tsv", "tsv");
        CSVWConfiguration config = CSVWConfiguration.builder().withDelimiter('\t').build();

        runMapperTest(access, config);
    }

    @Test
    public void test_00002a_tabs_unicode() throws SQLException, IOException {
        Access access = new LocalFileAccess("", "src/test/resources/csvw/mapper/tabs_unicode.tsv", "tsv");
        CSVWConfiguration config = CSVWConfiguration.builder()
                .withDelimiter('\u0009') // unicode for \t
                .build();

        runMapperTest(access, config);
    }

    @Test
    public void test_00002a_trim() throws SQLException, IOException {
        Access access = new LocalFileAccess("", "src/test/resources/csvw/mapper/trim.csv", "csv");
        CSVWConfiguration config = CSVWConfiguration.builder().withTrim(true).build();

        runMapperTest(access, config);
    }

    @Test
    public void test_1002a_null() throws SQLException, IOException {
        CSVWSourceIterator iterator = new CSVWSourceIterator();
        Access access = new LocalFileAccess("", "src/test/resources/csvw/mapper/null.csv", "csv");
        CSVWConfiguration config = CSVWConfiguration.builder()
                .withNulls(List.of("NULL"))
                .withDelimiter(';')
                .build();
        iterator.open(access, config);

        String[] header = new String[]{"ID", "Name"};
        CSVSource expected1 = new CSVSource(header, new String[]{"10", "Venus"}, access.getDataTypes()),
                expected2 = new CSVSource(header, new String[]{"11", null}, access.getDataTypes()),
                expected3 = new CSVSource(header, new String[]{"12", "Serena"}, access.getDataTypes()),
                expected4 = new CSVSource(header, new String[]{"13", "null"}, access.getDataTypes());


        assertTrue(compareIterator(iterator, Set.of(expected1, expected2, expected3, expected4)));
    }

    @Test
    public void test_1002a_nulls() throws SQLException, IOException {
        CSVWSourceIterator iterator = new CSVWSourceIterator();
        Access access = new LocalFileAccess("", "src/test/resources/csvw/mapper/nulls.csv", "csv");
        CSVWConfiguration config = CSVWConfiguration.builder()
                .withNulls(List.of("NULL", "null"))
                .withDelimiter(';')
                .build();

        iterator.open(access, config);

        String[] header = new String[]{"ID", "Name"};
        CSVSource e1 = new CSVSource(header, new String[]{"10", "Venus"}, access.getDataTypes()),
                e2 = new CSVSource(header, new String[]{null, "Richard"}, access.getDataTypes()),
                e3 = new CSVSource(header, new String[]{"12", "Serena"}, access.getDataTypes()),
                e4 = new CSVSource(header, new String[]{"13", null}, access.getDataTypes());

        assertTrue(compareIterator(iterator, Set.of(e1, e2, e3, e4)));
    }
}
