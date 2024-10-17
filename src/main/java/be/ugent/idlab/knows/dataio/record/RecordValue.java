package be.ugent.idlab.knows.dataio.record;

import java.util.Objects;

enum Status {
    OK,
    EMPTY,
    ERROR
}

public class RecordValue {
    private final Status status;
    private final Object value;
    private final String errorMessage;

    private RecordValue(Status status, Object value, final String errorMessage) {
        this.status = status;
        this.value = value;
        this.errorMessage = errorMessage;
    }

    /**
     * Creates an empty RecordValue. This is the result of an unknown reference or one that returns NULL.
     * @return An Empty RecordValue
     */
    public static RecordValue empty() {
        return new RecordValue(Status.EMPTY, null, null);
    }

    /**
     * Creates a RecordValue with a value. This is the result of a known reference returning a value.
     * @param value The value of the record after resolving the reference.
     * @return A non-empty RecordValue
     */
    public static RecordValue ok(Object value) {
        return new RecordValue(Status.OK, value, null);
    }

    /**
     * Creates a RecordValue indicating an error.
     * This is the result of an error occurring when the reference is being resolved.
     * @param message A description of the error.
     * @return A RecordValue that represents an error.
     */
    public static RecordValue error(String message) {
        return new RecordValue(Status.ERROR, null, message);
    }

    /**
     * Checks if this RecordValue is OK and has a non-null value.
     * @return {@code true} if OK and the value is not null.
     */
    public boolean isOk() {
        return status == Status.OK;
    }

    /**
     * Checks if this RecordValue represents an error. In this case {@link #getErrorMessage()} can be invoked
     * to get more information about the error.
     * @return {@code true} if not OK.
     */
    public boolean isError() {
        return status == Status.ERROR;
    }

    /**
     * Checks if this RecordValue represents an empty (null) value.
     * @return {@code true} if no error occurred but the value is empty (null).
     */
    public boolean isEmpty() {
        return status == Status.EMPTY;
    }

    /**
     * Gets the value contained in this RecordValue.
     * @return A non-null object when {@link #isOk()} returns {@code true}, or {@code null} if {@link #isError()}
     * or {@link #isEmpty()} return true.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Get the error message if this RecordValue represents an error.
     * @return The description of the error if {@link #isError()} returns {@code true},
     * or {@code null} if {@link #isError()} returns {@code false}.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecordValue that = (RecordValue) o;
        return status == that.status && Objects.equals(value, that.value) && Objects.equals(errorMessage, that.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, value, errorMessage);
    }
}
