package be.ugent.idlab.knows.dataio.record;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Objects;

/**
 * This class is a specific implementation of a record for XML.
 * Every record corresponds with an XML element in a data source.
 */
public class HTMLRecord extends Record {

    private final Element element;
    private final List<String> headers;

    public HTMLRecord(Element element, List<String> headers) {
        this.element = element;
        this.headers = headers;
    }

    /**
     * This method returns the objects for a reference (XPath) in the record.
     *
     * @param reference the reference for which objects need to be returned.
     * @return a list of objects for the reference.
     */
    @Override
    public RecordValue get(String reference) {
        int index = headers.indexOf(reference);
        if (index == -1) {
            return RecordValue.error(String.format("Mapping for %s not found, expected one of %s", reference, headers));
        }
        Elements tr = element.select("tr");
        if (tr.isEmpty()) {
            // TODO decent exception
            return RecordValue.error(String.format("Mapping for %s not found, expected one of %s", reference, headers));
        }
        Elements td = tr.get(0).select("td");
        if (td.size() <= index) {
            return RecordValue.empty();
        } else {
            return RecordValue.ok(td.get(index).text());
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;

        if (this == obj) return true;

        if (getClass() != obj.getClass()) return false;

        HTMLRecord o = (HTMLRecord) obj;

        return ((this.element != null && this.element.text().equals(o.element.text())) || (this.element == null && o.element == null)) &&
                ((this.headers != null && this.headers.equals(o.headers)) || (this.headers == null && o.headers == null));
    }

    @Override
    public int hashCode() {
        return Objects.hash(element, headers);
    }
}
