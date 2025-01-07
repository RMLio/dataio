package be.ugent.idlab.knows.dataio.utils;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CSVNullInjectorTest {
    private String getProcessedString(String inputString) throws IOException {
        InputStream input = new ByteArrayInputStream(inputString.getBytes(StandardCharsets.UTF_8));
        try (NewCSVNullInjector injector = new NewCSVNullInjector(
                input, ',', '"', StandardCharsets.UTF_8
        )) {
            return new String(injector.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /**
     * Tests a simple insertion in between two delimiters
     */
    @Test
    public void testInsertion() throws IOException {
        String testString = "ID,,Foo";
        String output = getProcessedString(testString);
        String expected = "ID,DATAIO_NULL_1,Foo";
        assertEquals(expected, output);
    }

    /**
     * Tests an insertion between two custom delimiters
     */
    @Test
    public void customDelimiter() throws IOException {
        String testString = "ID;;Foo";
        InputStream input = new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8));
        NewCSVNullInjector injector = new NewCSVNullInjector(input, ';', '"', StandardCharsets.UTF_8);

        String output = new String(injector.readAllBytes());
        String expected = "ID;DATAIO_NULL_1;Foo";
        assertEquals(expected, output);
    }

    /**
     * Tests injection of null value at the start of the string
     */
    @Test
    public void emptyStart() throws IOException {
        String testString = ",Foo,Bar";
        String output = getProcessedString(testString);
        String expected = "DATAIO_NULL_1,Foo,Bar";
        assertEquals(expected, output);
    }

    /**
     * Tests insertion of null value at the end of the string
     */
    @Test
    public void emptyEnd() throws IOException {
        String testString = "Foo,Bar,";
        String output = getProcessedString(testString);
        String expected = "Foo,Bar,DATAIO_NULL_1";
        assertEquals(expected, output);
    }

    /**
     * Tests the insertion in between a delimiter and a newline
     */
    @Test
    public void danglingSeparator() throws IOException {
        String testString = """
                "ID","Name","DateOfBirth"
                "1","Alice",
                "2","Bob","September, 2010"
                """;
        String expected = """
                "ID","Name","DateOfBirth"
                "1","Alice",DATAIO_NULL_1
                "2","Bob","September, 2010\"""";
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
        String expected = "\"aaa\",\"b\"\",,bb\",DATAIO_NULL_1,\"ccc\"";
        assertEquals(expected, output);
    }

    /**
     * Tests the injector's correct recognition of Linux newlines.
     */
    @Test
    public void unixNewLine() throws IOException {
        String testString = "Foo,,Bar\n,B";
        String output = getProcessedString(testString);
        String expected = "Foo,DATAIO_NULL_1,Bar\nDATAIO_NULL_2,B";
        assertEquals(expected, output);
    }

    /**
     * Tests the injector's correct recognition of Windows newlines.
     */
    @Test
    public void windowsNewLine() throws IOException {
        String testString = "Foo,,Bar\r\n,B";
        String output = getProcessedString(testString);
        String expected = "Foo,DATAIO_NULL_1,Bar\nDATAIO_NULL_2,B";
        assertEquals(expected, output);
    }

    @Test
    public void testReplaceNulls() {
        NewCSVNullInjector injector = new NewCSVNullInjector(new ByteArrayInputStream(new byte[0]), ',', null, StandardCharsets.UTF_8);
        assertEquals("DATAIO_NULL_1", injector.replaceNulls(""));
        assertEquals("test", injector.replaceNulls("test"));
        assertEquals("DATAIO_NULL_2" + ",test", injector.replaceNulls(",test"));
        assertEquals("test,DATAIO_NULL_3", injector.replaceNulls("test,"));
        assertEquals("DATAIO_NULL_4,DATAIO_NULL_5", injector.replaceNulls(","));
        assertEquals("DATAIO_NULL_6,DATAIO_NULL_7,DATAIO_NULL_8", injector.replaceNulls(",,"));
    }

    @Test
    public void testNewlineNull() throws IOException {
        String testString = "9,\"Vice President, N/A Strategic Accounts\",9,\"VP STRATEGIC ACCOUNTS, NA\n\nBeamery \n***truncated***\"";
        String output = getProcessedString(testString);
        String expected = "9,\"Vice President, N/A Strategic Accounts\",9,\"VP STRATEGIC ACCOUNTS, NA\n\nBeamery \n***truncated***\"";
        assertEquals(expected, output);
    }
}
