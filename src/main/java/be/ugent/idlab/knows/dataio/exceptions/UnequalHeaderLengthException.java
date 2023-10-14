package be.ugent.idlab.knows.dataio.exceptions;

/**
 * Exception to be thrown when the header and the row element count don't line up
 */
public class UnequalHeaderLengthException extends RuntimeException {

    private static final long serialVersionUID = -6181007946867658312L;

    /**
     * Constructor for the exception
     *
     * @param header string representation of the offending header
     * @param row    string representation of the offending row
     */
    public UnequalHeaderLengthException(String header, String row) {
        super(String.format("Header and row do not contain the same amount of fields!\nOffending header: %s\nOffending row: %s\n", header, row));
    }
}
