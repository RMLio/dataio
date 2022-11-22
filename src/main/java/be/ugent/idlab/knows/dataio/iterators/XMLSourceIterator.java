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

    private XdmSequenceIterator<net.sf.saxon.s9api.XdmItem> iterator;
    private XPathCompiler compiler;

    /**
     * This function loads the full file in at once.
     * Opens the files using the access object and initiates the iterator and compiler
     *
     * @param access          the corresponding access object
     * @param string_iterator string value used in the parser
     */
    public void open(Access access, String string_iterator) {
        // Saxon processor to be reused across XPath query evaluations
        Processor saxProcessor = new Processor(false);
        try {
            DocumentBuilder docBuilder = saxProcessor.newDocumentBuilder();
            XdmNode document = docBuilder.build(new StreamSource(access.getInputStream()));
            compiler = saxProcessor.newXPathCompiler();
            // Enable expression caching
            compiler.setCaching(true);
            // Extract and register existing source namespaces into the XPath compiler
            SaxNamespaceResolver.registerNamespaces(compiler, document);
            // Execute iterator XPath query
            XdmValue result = compiler.evaluate(string_iterator, document);
            iterator = result.iterator();
        } catch (SaxonApiException | IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return
     */
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

}
