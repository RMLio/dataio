package be.ugent.idlab.knows.dataio.iterators.xpath;

import org.w3c.dom.Document;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;

// source: https://howtodoinjava.com/java/xml/xpath-namespace-resolution-example/

public class NamespaceResolver implements NamespaceContext {

    //Store the source document to retrieve the existing namespaces
    private Document sourceDocument;

    public NamespaceResolver(Document document) {
        sourceDocument = document;
    }

    //The lookup for the namespace uris is delegated to the stored document.
    // TODO performance: verify check whether caching this would improve performance
    public String getNamespaceURI(String prefix) {
        if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX) || prefix.equals(XMLConstants.NULL_NS_URI)) {
            return sourceDocument.lookupNamespaceURI(null);
        }
        // xml: prefix is assumed to be known according to W3C: https://www.w3.org/TR/xml-names/
        else if (prefix.equals(XMLConstants.XML_NS_PREFIX)) {
            return XMLConstants.XML_NS_URI;
        } else {
            return sourceDocument.lookupNamespaceURI(prefix);
        }
    }

    public String getPrefix(String namespaceURI) {
        return sourceDocument.lookupPrefix(namespaceURI);
    }

    @SuppressWarnings("rawtypes")
    public Iterator getPrefixes(String namespaceURI) {
        return null;
    }
}
