package be.ugent.idlab.knows.dataio.exceptions;

public class BadHeaderException extends RuntimeException {
    private static final long serialVersionUID = -5337665888088989438L;

    public BadHeaderException(String path) {
        super(String.format("Header for file %s is contains empty values!", path));
    }
}
