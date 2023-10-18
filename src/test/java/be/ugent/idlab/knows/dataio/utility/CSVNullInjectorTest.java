package be.ugent.idlab.knows.dataio.utility;

import be.ugent.idlab.knows.dataio.utils.CSVNullInjector;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CSVNullInjectorTest {

    /**
     * Simple test to replace the value in between two commas
     */
    @Test
    public void testInsertion() throws IOException {
        String testString = "ID,,Foo";
        InputStream input = new ByteArrayInputStream(testString.getBytes());
        CSVNullInjector injector = new CSVNullInjector(input);

        // read out the injector
        String output = new String(injector.readAllBytes());
        assertEquals("ID,DATAIO_INJECTED_NULL_VALUE,Foo", output);
    }

    @Test
    public void customDelimiter() throws IOException {
        String testString = "ID;;Foo";
        InputStream input = new ByteArrayInputStream(testString.getBytes());
        CSVNullInjector injector = new CSVNullInjector(input, ';', '"');

        // read out the injector
        String output = new String(injector.readAllBytes());
        assertEquals("ID;DATAIO_INJECTED_NULL_VALUE;Foo", output);
    }

    /**
     * Tests ignoring quoted separators.
     */
    @Test
    public void ignoreQuotedSeparator() throws IOException {
        String testString = "ID,\",, ,\",Foo";
        InputStream input = new ByteArrayInputStream(testString.getBytes());
        CSVNullInjector injector = new CSVNullInjector(input);

        String output = new String(injector.readAllBytes());
        assertEquals( testString, output);
    }

    /**
     * Tests correct injection for escaped quotes
     */
    @Test
    public void escapedQuote() throws IOException {
        String testString = "\"aaa\",\"b\"\",,bb\",,\"ccc\"";
        InputStream input = new ByteArrayInputStream(testString.getBytes());
        CSVNullInjector injector = new CSVNullInjector(input);

        String output = new String(injector.readAllBytes());
        assertEquals("\"aaa\",\"b\"\",,bb\",DATAIO_INJECTED_NULL_VALUE,\"ccc\"", output);
    }
}
