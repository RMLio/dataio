package be.ugent.idlab.knows.dataio;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.iterators.CSVWSourceIterator;
import be.ugent.idlab.knows.dataio.iterators.csvw.CSVWConfiguration;
import be.ugent.idlab.knows.dataio.source.CSVSource;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class SerialisationTest {
    @Test
    public void csvwSerializable() throws IOException, SQLException {
        try {
            Access a = new LocalFileAccess("csv/0000.csv", "src/test/resources", "sometype");
            CSVWSourceIterator iterator = new CSVWSourceIterator();
            iterator.open(a, CSVWConfiguration.DEFAULT);

            Kryo kryo = new Kryo();
            kryo.setRegistrationRequired(false);
//            kryo.register(CSVWConfiguration.class);
//            kryo.register(CSVWSourceIterator.class);

            Output out = new Output(new FileOutputStream("serialized.bin"));
            kryo.writeObject(out, iterator);
            out.close();

            // verify Kryo can read the object
            Input in = new Input(new FileInputStream("serialized.bin"));
            CSVWSourceIterator iterator2 = kryo.readObject(in, CSVWSourceIterator.class);
            Assert.assertEquals(iterator2.next(), new CSVSource(new String[]{"ID", "Name"}, new String[]{"10", "Venus"}, a.getDataTypes()));
            in.close();
        } catch (Exception e) {
            throw e;
        } finally {
            File f = new File("serialized.bin");
            if (f.exists()) {
                f.delete();
            }
        }

    }
}
