package be.ugent.idlab.knows.dataio.iterators;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.VirtualAccess;
import be.ugent.idlab.knows.dataio.source.Source;

import java.io.*;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class JSONLinesSourceIterator extends SourceIterator {

    private static final long serialVersionUID = -6863412600310339167L;
    private final Access access;
    private final String iteratorPath;
    private transient LineIterator lineIterator;
    private transient JSONSourceIterator iterator;

    public JSONLinesSourceIterator(Access access, String iteratorPath) throws SQLException, IOException {
        this.access = access;
        this.iteratorPath = iteratorPath;

        this.bootstrap();
    }

    private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException, SQLException {
        inputStream.defaultReadObject();
        this.bootstrap();
    }

    private void bootstrap() throws SQLException, IOException {
        this.lineIterator = new LineIterator(access.getInputStream());
    }

    @Override
    public boolean hasNext() {
        while (this.iterator == null || !this.iterator.hasNext()) {
            // objects of current iterator had been exhausted, read next line
            if (this.lineIterator.hasNext()) {
                String line = this.lineIterator.next();
                try {
                    this.iterator = new JSONSourceIterator(new VirtualAccess(line.getBytes()), this.iteratorPath);
                } catch (SQLException | IOException e) {
                    // todo be more nice and gentle
                    throw new RuntimeException(e);
                }
            } else {
                return false; // no more lines can be read from the file
            }
        }
        return true;
    }

    @Override
    public Source next() {
        if (this.hasNext()) {
            return this.iterator.next();
        }
        throw new NoSuchElementException();
    }

    @Override
    public void close() throws IOException {
        this.lineIterator.close();
    }

    private static class LineIterator implements Iterator<String>, AutoCloseable {

        private final BufferedReader reader;
        private String currentLine = null;

        public LineIterator(InputStream inputStream) {
            this.reader = new BufferedReader(new InputStreamReader(inputStream));
        }

        @Override
        public boolean hasNext() {
            return currentLine != null || this.read() && this.currentLine != null;
        }

        @Override
        public String next() {
            if (this.hasNext()) {
                String line = this.currentLine;
                this.currentLine = null;

                return line;
            }

            throw new NoSuchElementException();
        }

        private boolean read() {
            try {
                if (reader.ready()) {
                    this.currentLine = reader.readLine();
                    return true;
                } else {
                    this.currentLine = null;
                    return false;
                }
            } catch (IOException e) {
                return false;
            }
        }

        @Override
        public void close() throws IOException {
            this.reader.close();
        }
    }
}

