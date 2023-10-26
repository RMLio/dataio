package be.ugent.idlab.knows.dataio.utility;

import be.ugent.idlab.knows.dataio.utils.CSVNullInjector;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CSVNullInjectorTest {
    private String getProcessedString(String inputString) throws IOException {
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        return new String(new CSVNullInjector(input, 1024 * 128).readAllBytes());
    }

    /**
     * Tests a simple insertion in between two delimiters
     * @throws IOException
     */
    @Test
    public void testInsertion() throws IOException {
        String testString = "ID,,Foo";
        String output = getProcessedString(testString);
        String expected = "ID,%s,Foo".replaceAll("%s", CSVNullInjector.NULL_VALUE);
        assertEquals(expected, output);
    }

    /**
     * Tests an insertion between two custom delimiters
     * @throws IOException
     */
    @Test
    public void customDelimiter() throws IOException {
        String testString = "ID;;Foo";
        InputStream input = new ByteArrayInputStream(testString.getBytes());
        CSVNullInjector injector = new CSVNullInjector(input, 1024 * 128,(byte) ';', (byte) '"');
        String output = new String(injector.readAllBytes());
        String expected = "ID;%s;Foo".replaceAll("%s", CSVNullInjector.NULL_VALUE);
        assertEquals(expected, output);
    }

    /**
     * Tests injection of null value at the start of the string
     * @throws IOException
     */
    @Test
    public void emptyStart() throws IOException {
        String testString = ",Foo,Bar";
        String output = getProcessedString(testString);
        String expected = "%s,Foo,Bar".replaceAll("%s", CSVNullInjector.NULL_VALUE);
        assertEquals(expected, output);
    }

    /**
     * Tests insertion of null value at the end of the string
     * @throws IOException
     */
    @Test
    public void emptyEnd() throws IOException {
        String testString = "Foo,Bar,";
        String output = getProcessedString(testString);
        String expected = "Foo,Bar,%s".replaceAll("%s", CSVNullInjector.NULL_VALUE);
        assertEquals(expected, output);
    }

    @Test
    public void danglingSeparator() throws IOException {
        String testString = """
                "ID","Name","DateOfBirth"
                "1","Alice",
                "2","Bob","September, 2010"
                """;
        String expected = """
                "ID","Name","DateOfBirth"
                "1","Alice",%s
                "2","Bob","September, 2010"
                """.replaceAll("%s", CSVNullInjector.NULL_VALUE);
        String actual = getProcessedString(testString);
        assertEquals(expected, actual);
    }

    /**
     * Tests ignoring of quoted separators
     */
    @Test
    public void ignoreQuotedSeparator() throws IOException {
        String testString = "ID,\",, ,\",Foo";
        String output = getProcessedString(testString);
        assertEquals(testString, output);
    }

    /**
     * Tests correct injection for escaped quotes
     * Input: "aaa","b"",,bb",,"ccc"
     * Output: "aaa","b"",,bb",${nullValue},"ccc"
     */
    @Test
    public void escapedQuote() throws IOException {
        String testString = "\"aaa\",\"b\"\",,bb\",,\"ccc\"";
        String output = getProcessedString(testString);
        String expected = "\"aaa\",\"b\"\",,bb\",%s,\"ccc\"".replaceAll("%s", CSVNullInjector.NULL_VALUE);
        assertEquals(expected, output);
    }

    /**
     * Tests the injector's correct recognition of Linux newlines.
     * @throws IOException
     */
    @Test
    public void unixNewLine() throws IOException {
        String testString = "Foo,,Bar\n,B";
        String output = getProcessedString(testString);
        String expected = "Foo,%s,Bar\n%s,B".replaceAll("%s", CSVNullInjector.NULL_VALUE);
        assertEquals(expected, output);
    }

    /**
     * Tests the injector's correct recognition of Windows newlines.
     * @throws IOException
     */
    @Test
    public void windowsNewLine() throws IOException {
        String testString = "Foo,,Bar\r\n,B";
        String output = getProcessedString(testString);
        String expected = "Foo,%s,Bar\r\n%s,B".replaceAll("%s", CSVNullInjector.NULL_VALUE);
        assertEquals(expected, output);
    }
}
