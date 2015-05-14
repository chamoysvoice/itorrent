package library.Utils;

/**
 * Created by leind on 14/05/15.
 */
public class UndefinedPathException extends Exception {
    /**
     * @serial The embedded exception if tunnelling, or null.
     */
    private Exception exception;

    /**
     * Create a new SAXException.
     */
    public UndefinedPathException () {super();}

    /**
     * Create a new UndefinedPathException.
     *
     * @param message The error or warning message.
     */
    public UndefinedPathException (String message) {
        super(message);
        this.exception = null;
    }


    /**
     * Create a new UndefinedPathException wrapping an existing exception.
     *
     * The existing exception will be embedded in the new
     * one, and its message will become the default message for
     * the UndefinedPathException.
     *
     * @param e The exception to be wrapped in a SAXException.
     */
    public UndefinedPathException (Exception e) {
        super();
        this.exception = e;
    }


    /**
     * Create a new SAXException from an existing exception.
     *
     * The existing exception will be embedded in the new
     * one, but the new exception will have its own message.
     *
     * @param message The detail message.
     * @param e The exception to be wrapped in a SAXException.
     */
    public UndefinedPathException (String message, Exception e) {
        super(message);
        this.exception = e;
    }


    /**
     * Return a detail message for this exception.
     *
     * If there is an embedded exception, and if the SAXException
     * has no detail message of its own, this method will return
     * the detail message from the embedded exception.
     *
     * @return The error or warning message.
     */
    public String getMessage () {
        String message = super.getMessage();

        if (message == null && exception != null) {
            return exception.getMessage();
        } else {
            return message;
        }
    }


    /**
     * Return the embedded exception, if any.
     *
     * @return The embedded exception, or null if there is none.
     */
    public Exception getException ()
    {
        return exception;
    }

    /**
     * Return the cause of the exception
     *
     * @return Return the cause of the exception
     */
    public Throwable getCause() {
        return exception;
    }

    /**
     * Override toString to pick up any embedded exception.
     *
     * @return A string representation of this exception.
     */
    public String toString () {
        if (exception != null) {
            return super.toString() + "\n" + exception.toString();
        } else {
            return super.toString();
        }
    }

}
