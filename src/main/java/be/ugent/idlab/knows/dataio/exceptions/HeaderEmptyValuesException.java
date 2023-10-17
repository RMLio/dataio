package be.ugent.idlab.knows.dataio.exceptions;

/**
 * An exception to be thrown when the header contains empty values.
 */
public class HeaderEmptyValuesException extends RuntimeException {
    private static final long serialVersionUID = -5337665888088989438L;

    public HeaderEmptyValuesException(String path) {
        super(String.format("Header for file %s is contains empty values!", path));
    }
}
