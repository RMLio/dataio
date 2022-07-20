package be.ugent.idlab.knows.source;

import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmValue;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a specific implementation of a record for XML.
 * Every record corresponds with an XML element in a data source.
 */
public class XMLSource extends Source {

    private XdmItem item;
    private XPathCompiler compiler;

    public XMLSource(XdmItem item, XPathCompiler compiler) {
        this.item = item;
        // Keep a reference to the XPath compiler for faster future queries
        this.compiler = compiler;
    }

    /**
     * This method returns the objects for a reference (XPath) in the record.
     *
     * @param value the reference for which objects need to be returned.
     * @return a list of objects for the reference.
     */
    @Override
    public List<Object> get(String value) {
        List<Object> results = new ArrayList<>();
        
        try {
            XdmValue result = compiler.evaluate(value, item);
            result.forEach((node) -> {
                results.add(node.getStringValue());  
            });
        } catch (SaxonApiException e1) {
            e1.printStackTrace();
        }


        return results;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;

        if(this == obj) return true;

        if(getClass() != obj.getClass()) return false;

        XMLSource o = (XMLSource) obj;
        return ((this.compiler != null && this.compiler.equals(o.compiler)) || (this.compiler == null && o.compiler == null))
                && ((this.item != null && this.item.equals(o.item)) || (this.item == null && o.item == null));
    }

    @Override
    public int hashCode() {
        return 1;
    }
}
