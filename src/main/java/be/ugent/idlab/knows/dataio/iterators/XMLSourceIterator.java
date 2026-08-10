package be.ugent.idlab.knows.dataio.iterators;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.xpath.SaxNamespaceResolver;
import be.ugent.idlab.knows.dataio.record.Record;
import be.ugent.idlab.knows.dataio.record.XMLRecord;
import net.sf.saxon.s9api.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * This class is a XMLSourceIterator that allows the iteration of a XML file
 */
public class XMLSourceIterator extends SourceIterator {
    @Serial
    private static final long serialVersionUID = 5027462468699419883L;
    private Access access;
    private final String stringIterator;
    private transient XdmSequenceIterator<XdmItem> iterator;
    private transient XPathCompiler compiler;
    private transient DocumentBuilder docBuilder;
    private final Map<String, String> namespaces;
    private int index = 0;

    public XMLSourceIterator(Access access, String stringIterator ) throws Exception {
        this(access, stringIterator, new HashMap<>()); 
    }

    public XMLSourceIterator(Access access, String stringIterator, Map<String, String> namespaces) throws Exception {
        this.access = access;
        this.stringIterator = stringIterator;
        this.namespaces = namespaces;
        bootstrap();
    }

    /**
     * Instantiates transient fields. This code needs to be run both at construction
     * time and after deserialization
     *
     * @throws IOException can be thrown due to the consumption of the input stream.
     *                     Same for SQLException.
     */
    private void bootstrap()
            throws SQLException, IOException, ParserConfigurationException, TransformerException, SaxonApiException {
        prepare();
        try (InputStream in = access.getInputStream()) {
            XdmNode document = docBuilder.build(new StreamSource(in));
            // Extract and register existing source namespaces into the XPath compiler
            SaxNamespaceResolver.registerNamespaces(this.compiler, document);
            // Execute iterator XPath query
            XdmValue result = compiler.evaluate(this.stringIterator, document);
            this.iterator = result.iterator();
        }
    }

    /**
     * Instantiates the parts that do not depend on the source: the Saxon processor, the
     * document builder and the XPath compiler. Keeping them alive is what lets the
     * compiler's expression cache survive, so that the iterator's XPath is compiled once
     * however many sources this iterator is pointed at.
     */
    private void prepare() throws SaxonApiException {
        if (this.compiler != null) {
            return;
        }

        // Saxon processor to be reused across XPath query evaluations
        Processor saxProcessor = new Processor(false);
        this.docBuilder = saxProcessor.newDocumentBuilder();
        this.compiler = saxProcessor.newXPathCompiler();
        // Enable expression caching
        this.compiler.setCaching(true);
        for (Map.Entry<String, String> entry : this.namespaces.entrySet()) {
            String uri = entry.getValue();
            String prefix = entry.getKey();
            this.compiler.declareNamespace(prefix, uri);
        }
    }

    /**
     * Points this iterator at another source and restarts the iteration, so that a single
     * iterator can run over many sources without rebuilding its XPath machinery. The
     * configuration it was constructed with, the iterator expression and the namespaces,
     * is kept.
     *
     * @param access the source to read next
     */
    @Override
    public void reset(Access access)
            throws SQLException, IOException, ParserConfigurationException, TransformerException, SaxonApiException {
        this.access = access;
        this.index = 0;
        bootstrap();
    }

    @Serial
    private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException, SQLException,
            ParserConfigurationException, SaxonApiException, TransformerException {
        inputStream.defaultReadObject();
        bootstrap();
    }

    @Override
    public Record next() {
        if (this.iterator.hasNext()) {
            return new XMLRecord(iterator.next(), compiler, index++);
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public void close() {
        this.iterator.close();
    }
}
