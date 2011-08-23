package sample;

public class NexentaStorException extends Exception {

    /**	serialVersionUID */
    private static final long serialVersionUID = 1L;

    public NexentaStorException(Exception e) {
        initCause(e);
    }

    public NexentaStorException(String message) {
        super(message);
    }

}
