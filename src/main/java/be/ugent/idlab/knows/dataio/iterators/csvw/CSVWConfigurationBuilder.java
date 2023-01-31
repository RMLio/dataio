package be.ugent.idlab.knows.dataio.iterators.csvw;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CSVWConfigurationBuilder {
    private char delimiter = ',';
    private char escapeCharacter = '\\';
    private String trim = "false";
    private char quoteCharacter = '"';
    private boolean skipHeader = false;
    private String commentPrefix = "#";
    private List<String> header = List.of();
    private List<String> nulls = List.of();

    private Charset encoding = StandardCharsets.UTF_8;

    public CSVWConfigurationBuilder withDelimiter(char delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    public CSVWConfigurationBuilder withEncoding(Charset encoding) {
        this.encoding = encoding;
        return this;
    }

    public CSVWConfigurationBuilder withEscapeCharacter(char escapeCharacter) {
        this.escapeCharacter = escapeCharacter;
        return this;
    }

    public CSVWConfigurationBuilder withTrim(String trim) {
        this.trim = trim;
        return this;
    }

    public CSVWConfigurationBuilder withTrim(boolean trim) {
        this.trim = Boolean.toString(trim);
        return this;
    }

    public CSVWConfigurationBuilder withQuoteCharacter(char quoteCharacter) {
        this.quoteCharacter = quoteCharacter;
        return this;
    }

    public CSVWConfigurationBuilder skipHeader(boolean skipHeader) {
        this.skipHeader = skipHeader;
        return this;
    }

    public CSVWConfigurationBuilder withCommentPrefix(String commentPrefix) {
        this.commentPrefix = commentPrefix;
        return this;
    }

    public CSVWConfigurationBuilder withHeader(List<String> header) {
        this.header = header;
        return this;
    }

    public CSVWConfigurationBuilder withNulls(List<String> nulls) {
        this.nulls = nulls;
        return this;
    }

    public CSVWConfiguration build() {
        return new CSVWConfiguration(
                this.delimiter,
                this.escapeCharacter,
                this.trim,
                this.quoteCharacter,
                this.skipHeader,
                this.commentPrefix,
                this.header,
                this.nulls,
                this.encoding
        );
    }


}
