package be.ugent.idlab.knows.source;

import org.jsoup.nodes.Element;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is a specific implementation of a record for XML.
 * Every record corresponds with an XML element in a data source.
 */
public class HTMLSource extends Source {

    private Element element;
    private List<String> headers;

    public HTMLSource(Element element, List<String> headers) {
        this.element = element;
        this.headers = headers;
    }

    /**
     * This method returns the objects for a reference (XPath) in the record.
     * @param value the reference for which objects need to be returned.
     * @return a list of objects for the reference.
     */
    @Override
    public List<Object> get(String value) {
        int index = headers.indexOf(value);

        return element.select("tr")
                .stream()
                .map(row -> row.select("td").get(index).text())
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;

        if(this == obj) return true;

        if(getClass() != obj.getClass()) return false;

        HTMLSource o = (HTMLSource) obj;

        return ((this.element != null && this.element.equals(o.element)) || (this.element == null && o.element == null)) &&
                ((this.headers != null && this.headers.equals(o.headers)) || (this.headers == null && o.headers == null));
    }

    @Override
    public int hashCode() {
        //TODO
        return 1;
    }
}
