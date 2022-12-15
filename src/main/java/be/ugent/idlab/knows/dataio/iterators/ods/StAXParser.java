package be.ugent.idlab.knows.dataio.iterators.ods;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;

public class StAXParser {
    public XMLEvent cursor;
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLEventReader reader;

    public StAXParser(InputStream inputStream) throws XMLStreamException {
        this.reader = factory.createXMLEventReader(inputStream);
        advance();
    }

    /**
     * Goes through the document until it encounters an attribute with the desired name.
     * If during reading end of document is encountered, cursor will be set to null.
     *
     * @param prefix prefix of the sought after element
     * @param local  local part of the sought after element
     */
    public void forwardToStartElement(String prefix, String local) {
        while (reader.hasNext()) {
            advance();
            if (cursor.isStartElement()) {
                StartElement e = cursor.asStartElement();
                QName name = e.getName();
                if (name.getPrefix().equals(prefix) && name.getLocalPart().equals(local)) {
                    return;
                }
            }
        }
        cursor = null;
    }

    public void advance() {
        try {
            if (reader.hasNext()) {
                cursor = reader.nextEvent();
            } else {
                cursor = null;
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }
}
