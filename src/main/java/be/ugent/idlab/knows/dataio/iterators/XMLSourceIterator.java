package be.ugent.idlab.knows.dataio.iterators;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.xpath.SaxNamespaceResolver;
import be.ugent.idlab.knows.dataio.source.Source;
import be.ugent.idlab.knows.dataio.source.XMLSource;
import net.sf.saxon.s9api.*;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.NoSuchElementException;

/**
 * This class is a XMLSourceIterator that allows the iteration of a XML file
 */
public class XMLSourceIterator extends SourceIterator {

    private final XdmSequenceIterator<XdmItem> iterator;
    private final XPathCompiler compiler;

    public XMLSourceIterator(Access access, String stringIterator) throws SQLException, IOException, SaxonApiException {
        // Saxon processor to be reused across XPath query evaluations
        Processor saxProcessor = new Processor(false);
        DocumentBuilder docBuilder = saxProcessor.newDocumentBuilder();
        XdmNode document = docBuilder.build(new StreamSource(access.getInputStream()));
        this.compiler = saxProcessor.newXPathCompiler();
        // Enable expression caching
        this.compiler.setCaching(true);
        // Extract and register existing source namespaces into the XPath compiler
        SaxNamespaceResolver.registerNamespaces(this.compiler, document);
        // Execute iterator XPath query
        XdmValue result = compiler.evaluate(stringIterator, document);
        this.iterator = result.iterator();
    }

    @Override
    public Source next() {
        if (this.iterator.hasNext()) {
            return new XMLSource(iterator.next(), compiler);
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
