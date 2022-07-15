package be.ugent.idlab.knows;

import be.ugent.idlab.knows.access.Access;
import be.ugent.idlab.knows.access.LocalFileAccess;
import be.ugent.idlab.knows.source.CSVSource;
import be.ugent.idlab.knows.source.ExcelSource;
import be.ugent.idlab.knows.source.Source;
import be.ugent.idlab.knows.source.SourceIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;


public class TestCore {

    private static final Logger logger = LoggerFactory.getLogger(TestCore.class);

    public void compareIterator(SourceIterator factory, Set<Source> expectedSources){

//        System.out.println("");
//        System.out.println("Begin set printing");
//        expectedSources.forEach(s ->  ((ExcelSource) s).printString());
//        System.out.println("End set printing");
//        System.out.println("");

        while(factory.hasNext()) {
            CSVSource csvSource = (CSVSource) factory.nextSource();
            csvSource.getData().forEach((a,b) -> System.out.println(a + ": " + b));
//            System.out.println("");
//            System.out.println("next source");
//            ExcelSource excelSource = (ExcelSource) factory.nextSource();
//            excelSource.printString();
//            System.out.println("end next source");
//            System.out.println("");
            assert expectedSources.contains(csvSource);
//            System.out.println("Assert worked");
        }
    }

    public Access makeLocalAccess(String inputFile){
        return new LocalFileAccess(getClass().getResource(inputFile).getPath(), "");
    }
}
