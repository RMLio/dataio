package be.ugent.idlab.knows.dataio.cores;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.source.Source;
import be.ugent.idlab.knows.dataio.streams.SourceStream;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Iterator;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class StreamTestCore extends TestCore {

    protected static Iterator<Source> getIterator(SourceStream stream) {
        return stream.getStream().iterator();
    }

    protected void runTest(String path, String basePath, String fileType, String encoding, Class<? extends SourceStream> classObj, Evaluator evaluator) {
        try {
            Iterator<Source> iterator = prepareIterator(path, basePath, fileType, encoding, classObj);
            assertTrue(evaluator.evaluate(iterator));
        } catch (SQLException | IOException e) {
            logger.error("An unexpected exception was thrown during the test!", e);
            fail();
        }
    }

    protected Iterator<Source> prepareIterator(String path, String basePath, String filetype, String encoding, Class<? extends SourceStream> streamClass, Object... initArgs) throws SQLException, IOException {
        Access access = this.makeLocalAccess(path, basePath, filetype, encoding);
        try {
            Constructor<?>[] constructors = streamClass.getConstructors();

            if (constructors.length > 1) {
                throw new RuntimeException("There is more than one public constructor declared!");
            }

            if (constructors.length == 0) {
                throw new RuntimeException("Stream class must have exactly one public constructor!");
            }

            SourceStream s = streamClass.cast(streamClass.getConstructors()[0].newInstance(initArgs));
            return getIterator(s);

        } catch (InstantiationException ie) {
            throw new RuntimeException("The passed streamClass must be a concrete class!", ie);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("The constructor for passed streamClass can't be accessed!", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("An exception was thrown in the constructor of streamClass!", e);
        } catch (ClassCastException e) {
            throw new RuntimeException("Class provided as argument cannot be cast to instance of SourceStream!", e);
        }
    }
}
