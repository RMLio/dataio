package be.ugent.idlab.knows.source;

public interface SourceIterator {

    public Source nextSource();

    public boolean hasNext();

//    Stream<Source> getRecords(Access access) throws IOException, SQLException, ClassNotFoundException, Exception;
}
