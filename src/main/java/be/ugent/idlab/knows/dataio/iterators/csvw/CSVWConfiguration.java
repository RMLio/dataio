package be.ugent.idlab.knows.dataio.iterators.csvw;


import org.simpleflatmapper.lightningcsv.CsvParser;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;

import java.io.Serializable;
import java.util.List;

/**
 * Class representing a configuration for parsing of CSVW files.
 * Use {@link CSVWConfigurationBuilder#build()} for creating this class.<br>
 * In the current implementation, we support:
 * <ul>
 * <li> custom delimiter, ',' by default </li>
 * <li> custom escape character, '"' by default </li>
 * <li> trimming whitespace off of the values, "false" by default </li>
 * <li> custom quote characters, '"' by default </li>
 * <li> custom comment prefix, "#" by default </li>
 * <li> skipping the header, "false" by default </li>
 * <li> custom header, by default an empty list </li>
 * <li> a list of null fields: by default empty </li>
 * </ul>
 * As such, the default configuration parses regular CSV files.
 */
public final class CSVWConfiguration implements Serializable {
    private static final long serialVersionUID = -5750213407136895070L;
    public static CSVWConfiguration DEFAULT = CSVWConfiguration.builder().build();
    private final char delimiter;
    private final char escapeCharacter;
    private final String trim;
    private final char quoteCharacter;
    private final boolean skipHeader;
    private final String commentPrefix;
    private final List<String> header;
    private final List<String> nulls;
    private final String encoding;

    CSVWConfiguration(char delimiter, char escapeCharacter, String trim, char quoteCharacter, boolean skipHeader, String commentPrefix, List<String> header, List<String> nulls, String encoding) {
        // opencsv parser options
        this.delimiter = delimiter;
        this.escapeCharacter = escapeCharacter;
        this.quoteCharacter = quoteCharacter;

        // rest of the dialect that need to be built and checked by hand
        this.trim = trim;
        this.skipHeader = skipHeader;
        this.commentPrefix = commentPrefix;
        this.header = header;
        this.nulls = nulls;
        this.encoding = encoding;
    }

    public static CSVWConfigurationBuilder builder() {
        return new CSVWConfigurationBuilder();
    }

    public char getDelimiter() {
        return delimiter;
    }

    public char getEscapeCharacter() {
        return escapeCharacter;
    }

    public String getTrim() {
        return trim;
    }

    public char getQuoteCharacter() {
        return quoteCharacter;
    }

    public boolean isSkipHeader() {
        return skipHeader;
    }

    public String getCommentPrefix() {
        return commentPrefix;
    }

    public List<String> getHeader() {
        return header;
    }

    public List<String> getNulls() {
        return this.nulls;
    }

    public String getEncoding() {
        return encoding;
    }

    public CsvParser.DSL getSFMParser() {
        return CsvParser
                .separator(this.delimiter)
                .escape(this.escapeCharacter)
                .quote(this.quoteCharacter);
    }

    public CSVParser getOpenCSVParser() {
        return new CSVParserBuilder()
                .withSeparator(this.delimiter)
                .withEscapeChar(this.escapeCharacter)
                .withQuoteChar(this.quoteCharacter)
                .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                .build();
    }
}
