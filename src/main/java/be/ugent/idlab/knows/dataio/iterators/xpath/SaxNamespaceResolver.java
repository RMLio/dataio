package be.ugent.idlab.knows.dataio.iterators.xpath;

import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaxNamespaceResolver {
    private static final Logger log = LoggerFactory.getLogger(SaxNamespaceResolver.class);

    /**
     * Hackish method to extract all available namespace definitions
     * using a XPath expression and declaring them into a XPath Compiler.
     * Uses the {@link net.sf.saxon.om.NamespaceResolver} interface
     * relying on saxon-based utility classes. 
     */
    public static void registerNamespaces(XPathCompiler compiler, XdmNode node) {
        try {
            String query = "//namespace::*";
            XdmValue result = compiler.evaluate(query, node);

            result.forEach((item) -> {
                NodeInfo ns = (NodeInfo) item.getUnderlyingValue();
                compiler.declareNamespace(ns.getLocalPart(), ns.getStringValue());
            });

            // Add additional function namespaces. These should probably be defined as constants. 
            compiler.declareNamespace("math", "http://www.w3.org/2005/xpath-functions/math");
            compiler.declareNamespace("map", "http://www.w3.org/2005/xpath-functions/map");
            compiler.declareNamespace("array", "http://www.w3.org/2005/xpath-functions/array");

        } catch (SaxonApiException e) {
            log.warn("Could not register namespaces.", e);
        }
    }
}
