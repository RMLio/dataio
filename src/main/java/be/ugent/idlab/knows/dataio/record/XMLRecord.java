package be.ugent.idlab.knows.dataio.record;

import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class is a specific implementation of a record for XML.
 * Every record corresponds to an XML element in a data source.
 */
public class XMLRecord extends Record {

    private final XdmItem item;
    private final XPathCompiler compiler;
    private final int index; // the index in an array this element is at


    /**
     * Creates an XMLRecord.
     * @param item      The item in the XDM data model; the "current document"
     * @param compiler  Holds static context for a compiled XPath.
     * @param index     The index of the item in the global document.
     */
    public XMLRecord(XdmItem item, XPathCompiler compiler, int index) {
        this.item = item;
        // Keep a reference to the XPath compiler for faster future queries
        this.compiler = compiler;
        this.index = index;
    }

    /**
     * This method returns the objects for a reference (XPath) in the record.
     *
     * @param reference the reference for which objects need to be returned.
     * @return a list of objects for the reference.
     */
    @Override
    public RecordValue get(String reference) {
        try {
            XdmValue result = compiler.evaluate(reference, item);
            if (result.isEmpty()) {
                return RecordValue.empty();
            } else {
                List<String> results = new ArrayList<>();
                result.forEach((node) -> results.add(node.getStringValue()));
                return RecordValue.ok(results);
            }
        } catch (SaxonApiException e1) {
            return RecordValue.error(e1.getMessage());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XMLRecord xmlRecord = (XMLRecord) o;

        return itemEquals(xmlRecord.item);
    }

    /**
     * Compares an item to the item of this.
     * This method is implemented due to the lack of proper equals() method in the XdmItem class we rely on.
     * Two XdmItems are considered equivalent if their string values are equal
     *
     * @param item item to compare
     * @return true if the items are equivalent, false otherwise
     */
    private boolean itemEquals(XdmItem item) {
        return this.item.getStringValue().matches(item.getStringValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, compiler);
    }

    public XdmItem getItem() {
        return item;
    }

    public int getIndex() {
        return index;
    }
}
