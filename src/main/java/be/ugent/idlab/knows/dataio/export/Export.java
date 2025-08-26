package be.ugent.idlab.knows.dataio.export;

import java.io.OutputStream;

/**
 * Interface representing outputting to a data sink.
 */
public interface Export {
    OutputStream getOutputStream() throws Exception;
}
