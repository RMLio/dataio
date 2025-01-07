package be.ugent.idlab.knows.dataio.record;

import java.util.Objects;

enum Status {
    OK,
    EMPTY,
    NOT_FOUND,
    ERROR
}

public class RecordValue {
    private final Status status;
    private final Object value;
    private final String message;

    private RecordValue(Status status, Object value, final String message) {
        this.status = status;
        this.value = value;
        this.message = message;
    }

    /**
     * Creates an empty RecordValue. This is the result of a reference that returns NULL.
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
     * Creates an empty RecordValue. This is the result of a reference that cannot be found in the data.
     * @param message The reason why the reference could not be found.
     * @return an Empty RecordValue
     */
    public static RecordValue notFound(final String message) {
        return new RecordValue(Status.NOT_FOUND, null, message);
    }

    /**
     * Creates a RecordValue indicating an error.
     * This is the result of an error occurring when the reference is being resolved.
     * @param message A description of the error.
     * @return A RecordValue that represents an error.
     */
    public static RecordValue error(final String message) {
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
     * Checks if this RecordValue represents an error. In this case {@link #getMessage()} can be invoked
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
     * Checks if this RecordValue represents an empty (null) value because the reference could not be found.
     * @return {@code true} if no error occurred but the value is empty (null).
     */
    public boolean isNotFound() {
        return status == Status.NOT_FOUND;
    }

    /**
     * Gets the value contained in this RecordValue.
     * @return A non-null object when {@link #isOk()} returns {@code true}, or {@code null} if {@link #isError()},
     * {@link #isEmpty()} or {@link #isNotFound()} return true.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Get the error message if this RecordValue represents an error.
     * @return The description of the error if {@link #isError()} or {@link #isNotFound()} returns {@code true},
     * or {@code false} otherwise.
     */
    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecordValue that = (RecordValue) o;
        return status == that.status && Objects.equals(value, that.value) && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, value, message);
    }
}
