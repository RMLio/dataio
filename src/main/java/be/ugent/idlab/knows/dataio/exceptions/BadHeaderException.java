package be.ugent.idlab.knows.dataio.exceptions;

public class BadHeaderException extends RuntimeException {
    public BadHeaderException(String header) {
        super(String.format("The provided header is faulty! Header provided: %s", header));
    }
}
