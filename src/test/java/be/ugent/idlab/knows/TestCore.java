package be.ugent.idlab.knows;

import be.ugent.idlab.knows.access.Access;
import be.ugent.idlab.knows.access.LocalFileAccess;
import be.ugent.idlab.knows.iterator.CSVTest;
import be.ugent.idlab.knows.iterators.*;
import be.ugent.idlab.knows.source.CSVSource;
import be.ugent.idlab.knows.source.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import be.ugent.idlab.knows.utils.Utils.*;

import java.util.*;

import static be.ugent.idlab.knows.utils.Utils.typeToEncoding;
import static org.apache.commons.compress.utils.FileNameUtils.getExtension;

public class TestCore {
    private static final Logger logger = LoggerFactory.getLogger(CSVTest.class);


    private List<SourceIterator> iterators = List.of(new CSVSourceIterator(), new CSVWSourceIterator(),
            new ExcelSourceIterator(), new HTMLSourceIterator(), new ODSSourceIterator(), new XMLSourceIterators());

    public void evaluate_0000(SourceIterator iterator, boolean numeric){
        List<String> header = List.of("ID", "Name");

        List<Map<String, Object>> list = List.of(makeMap(header, List.of(List.of(numeric ? 10 : "10"), List.of("Venus"))));

        compareIterator(iterator, list, header);
    }

    public void evaluate_0001(SourceIterator iterator){
        List<String> header = List.of("name", "description", "address");

        ArrayList<Map<String, Object>> list = new ArrayList<>();
        list.add(makeMap(header, List.of(List.of("Trollekelder"),List.of("Beer café in the shadows of the St James' church"),
                List.of("Bij Sint-Jacobs 17 9000 Gent Belgium"))));
        list.add(makeMap(header, List.of(List.of("Hot Club Gent"),List.of("Live jazz concerts in an intimate setting"),
                List.of("Schuddevisstraatje 2 - Groentenmarkt 9000 Gent Belgium"))));
        list.add(makeMap(header,List.of(List.of("Jan van Gent"), List.of("Quirky local pub with a popular summer terrace"),
                List.of("Annonciadenstraat 1 9000 Gent Belgium"))));
        compareIterator(iterator, list, header);
    }

    public void evaluate_1001_header_long(SourceIterator iterator){
        List<String> header = List.of("name", "description", "address", "extra");

        ArrayList<Map<String, Object>> list = new ArrayList<>();
        list.add(makeMap(header, List.of(List.of("Trollekelder"),List.of("Beer café in the shadows of the St James' church"),
                List.of("Bij Sint-Jacobs 17 9000 Gent Belgium"), List.of())));
        list.add(makeMap(header, List.of(List.of("Hot Club Gent"),List.of("Live jazz concerts in an intimate setting"),
                List.of("Schuddevisstraatje 2 - Groentenmarkt 9000 Gent Belgium"), List.of())));
        list.add(makeMap(header,List.of(List.of("Jan van Gent"), List.of("Quirky local pub with a popular summer terrace"),
                List.of("Annonciadenstraat 1 9000 Gent Belgium"), List.of())));

        compareIterator(iterator, list, header);
    }

    public void evaluate_1001_header_short(SourceIterator iterator){
        List<String> header = List.of("name", "description");

        ArrayList<Map<String, Object>> list = new ArrayList<>();
        list.add(makeMap(header, List.of(List.of("Trollekelder"),List.of("Beer café in the shadows of the St James' church"))));
        list.add(makeMap(header, List.of(List.of("Hot Club Gent"),List.of("Live jazz concerts in an intimate setting"))));
        list.add(makeMap(header,List.of(List.of("Jan van Gent"), List.of("Quirky local pub with a popular summer terrace"))));

        compareIterator(iterator, list, header);
    }



    public void compareIterator(SourceIterator iterator, Set<Source> expectedSources){
        int counter = 0;
        while(iterator.hasNext()) {
            counter++;
//            Iterator<Source> it = expectedSources.iterator();
//            Source el1 = it.next();
//            Source el2= it.next();
//            Source el3= it.next();
//            Source el4= it.next();
//
//            Source r1 = iterator.next();
//            Source r2= iterator.next();
//            Source r3= iterator.next();
//            Source r4= iterator.next();
//            System.out.println(el2 +""+ el1);

            assert expectedSources.contains(iterator.next());
        }
        assert expectedSources.size() == counter;
    }

    public Access makeLocalAccess(String inputFile, String base, String type, String encoding){
        return new LocalFileAccess(getClass().getResource(inputFile).getPath(), base, type, encoding);
    }

    private HashMap<String, Object> makeMap(List<String> header, List<Object> data){
        HashMap<String, Object> map = new HashMap<>();
        for(int i = 0; i < header.size(); i++){
            map.put(header.get(i), data.get(i));
        }
        return map;
    }

    private void compareIterator(SourceIterator iterator, List<Map<String, Object>> list, List<String> header){
        int counter = 0;

        while(iterator.hasNext()){
            counter++;
            Source source = iterator.next();
            assert compareList(source, list, header);
        }
        assert counter == list.size();
    }

    private boolean compareList(Source source, List<Map<String, Object>> checkList, List<String> header){
        for(Map<String, Object> map: checkList){
            if(compareMap(source, map, header)) return true;
        }
        return false;
    }

    private boolean compareMap(Source source, Map<String, Object> checkMap, List<String> header){
        for(String value: header){
            if(! checkMap.containsKey(value)) return false;

            if(checkMap.get(value) == null){
                if(source.get(value) != null) return false;
            }else if(! checkMap.get(value).equals(source.get(value))) return false;
        }
        return true;
    }
}