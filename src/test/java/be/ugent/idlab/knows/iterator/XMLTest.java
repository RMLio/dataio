package be.ugent.idlab.knows.iterator;

import be.ugent.idlab.knows.TestCore;
import be.ugent.idlab.knows.iterators.XMLSourceIterators;
import org.junit.Test;

public class XMLTest extends TestCore {

    @Test
    public void evaluate_0000_XML(){
        XMLSourceIterators xmlSourceIterators = new XMLSourceIterators();
        xmlSourceIterators.open(makeLocalAccess("/xml/0000.xml","", "xml", "utf-8"), "students/student");
        evaluate_0000(xmlSourceIterators, false);
    }

    @Test
    public void evaluate_0001_XML(){
        XMLSourceIterators xmlSourceIterators = new XMLSourceIterators();
        xmlSourceIterators.open(makeLocalAccess("/xml/0001.xml","", "xml", "utf-8"), "pubs/pub");
        evaluate_0001(xmlSourceIterators);
    }

}
