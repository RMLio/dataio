package be.ugent.idlab.knows.dataio.record;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

/**
 * This class is a specific implementation of a record for XML.
 * Every record corresponds with an XML element in a data source.
 */
public class HTMLRecord extends Record {

    private Element element;
    private List<String> headers;

    public HTMLRecord(Element element, List<String> headers) {
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
        if(index == -1){
            throw new IllegalArgumentException(String.format("Mapping for %s not found, expected one of %s", value, headers));
        }
        Elements tr = element.select("tr");
        if(tr.size() == 0){
            // TODO decent exception
            throw new IllegalArgumentException(String.format("Mapping for %s not found, expected one of %s", value, headers));
        }
        Elements td = tr.get(0).select("td");
        if(td.size() <= index){
            return List.of();
        }
        return List.of(td.get(index).text());
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;

        if(this == obj) return true;

        if(getClass() != obj.getClass()) return false;

        HTMLRecord o = (HTMLRecord) obj;

        return ((this.element != null && this.element.text().equals(o.element.text())) || (this.element == null && o.element == null)) &&
                ((this.headers != null && this.headers.equals(o.headers)) || (this.headers == null && o.headers == null));
    }

    @Override
    public int hashCode() {
        //TODO
        return 1;
    }
}
