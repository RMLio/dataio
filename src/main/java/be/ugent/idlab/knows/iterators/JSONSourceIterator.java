package be.ugent.idlab.knows.iterators;

import be.ugent.idlab.knows.access.Access;
import be.ugent.idlab.knows.source.JSONSource;
import be.ugent.idlab.knows.source.Source;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JsonProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * This class is a record factory that creates JSON records.
 */
public class JSONSourceIterator extends SourceIterator {
    private Iterator<String> iterator;
    private Object document;

    public void open(Access access, String string_iterator){
        Object document = null;
        try {
            document = getDocumentFromStream(access.getInputStream(), access.getContentType());
        } catch (IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        this.document = document;


        Configuration conf = Configuration.builder()
                .options(Option.AS_PATH_LIST).build();

        // This JSONPath library specifically cannot handle keys with commas, so we need to escape it
        String escapedIterator = string_iterator.replaceAll(",", "\\\\,");

        iterator = ((List<String>) JsonPath.using(conf).parse(document).read(escapedIterator)).iterator();
    }

//    /**
//     * This method returns the records from a JSON document based on an iterator.
//     * @param document the document from which records need to get.
//     * @param iterator the used iterator.
//     * @return a list of records.
//     */
//    List<Source> getRecordsFromDocument(Access access, String contentType, String iterator) {
//
//        List<Source> records = new ArrayList<>();
//
//        Configuration conf = Configuration.builder()
//                .options(Option.AS_PATH_LIST).build();
//
//        // This JSONPath library specifically cannot handle keys with commas, so we need to escape it
//        String escapedIterator = iterator.replaceAll(",", "\\\\,");
//
//        try {
//            List<String> pathList = JsonPath.using(conf).parse(document).read(escapedIterator);
//
//            for(String p :pathList) {
//                records.add(new JSONSource(document, p));
//            }
//        } catch (JsonPathException e) {
//            logger.warn("{} for iterator {}", e.getMessage(), iterator, e);
//        }
//
//        return records;
//    }

    /**
     * This method returns a JSON document from an InputStream.
     * @param stream the used InputStream.
     * @return a JSON document.
     * @throws IOException
     */
    Object getDocumentFromStream(InputStream stream) throws IOException {
        return Configuration.defaultConfiguration().jsonProvider().parse(stream, "utf-8");
    }

    Object getDocumentFromStream(InputStream stream, String contentType) throws IOException {
        if(contentType.equalsIgnoreCase("jsonl")){
            JsonProvider provider = Configuration.defaultConfiguration().jsonProvider();
            BufferedReader lineReader = new BufferedReader(new InputStreamReader(stream));
            Object items = provider.createArray();
            int index = 0;
            while (lineReader.ready()){
                provider.setArrayIndex(items, index, provider.parse(lineReader.readLine()));
                index += 1;
            }
            return items;
        } else {
            return getDocumentFromStream(stream);
        }
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Source next() {
        if(iterator.hasNext()){
            return new JSONSource(document, iterator.next());
        } else {
            throw new NoSuchElementException();
        }
    }
}
