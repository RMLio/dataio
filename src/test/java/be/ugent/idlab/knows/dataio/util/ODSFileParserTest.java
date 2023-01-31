package be.ugent.idlab.knows.dataio.util;

import be.ugent.idlab.knows.dataio.iterators.ods.ODSFileParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public class ODSFileParserTest {
    private ODSFileParser parser;

    private ODSFileParser getDefaultParser() throws XMLStreamException, IOException {
        return ODSFileParser.newInstance("src/test/resources/ods/0001.ods");
    }

    @BeforeEach
    public void before() throws XMLStreamException, IOException {
        this.parser = getDefaultParser();
    }

    @Test
    public void readHeaderFirstTable() {
        // header of first table is read automatically
        String[] expectedHeader = new String[]{"name", "description", "address"};
        Assertions.assertArrayEquals(expectedHeader, this.parser.getHeader());
    }

    @Test
    public void readFirstDataRow() {
        Assertions.assertTrue(this.parser.canReadNextRow());
        String[] actual = this.parser.readNextRow();

        String[] expected = new String[]{"Trollekelder", "Beer café in the shadows of the St James' church", "Bij Sint-Jacobs 17 9000 Gent Belgium"};
        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    public void readSecondDataRow() {
        String[] expected = new String[]{"Hot Club Gent", "Live jazz concerts in an intimate setting", "Schuddevisstraatje 2 - Groentenmarkt 9000 Gent Belgium"};
        // skip first data row
        this.parser.moveToNextRow();

        String[] actual = this.parser.readNextRow();
        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    public void getHeaderSecondTable() {
        String[] expected = new String[]{"ID", "Name"};
        Assertions.assertTrue(this.parser.moveToNextTable());

        Assertions.assertArrayEquals(expected, this.parser.getHeader());
    }

    static class MapperTests {

        ODSFileParser parser;

        @Test
        public void onlyHeaderNoRows() throws XMLStreamException, IOException {
            this.parser = ODSFileParser.newInstance("src/test/resources/ods/mapper_tests/ODS-0000.ods");

            String[] header = new String[]{"Name"};
            Assertions.assertArrayEquals(header, this.parser.getHeader());

            String[] row = this.parser.readNextRow();

            Assertions.assertNull(row);

            // second attempt
            row = this.parser.readNextRow();
            Assertions.assertNull(row);
        }
    }
}
