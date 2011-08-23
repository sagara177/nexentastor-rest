package rest.client.exception;

public class RestClientException extends Exception {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public RestClientException() {
    }

    public RestClientException(Throwable t) {
        super(t);
    }

    public RestClientException(String message) {
        super(message);
    }

    public RestClientException(String message, Throwable t) {
        super(message, t);
    }
}
