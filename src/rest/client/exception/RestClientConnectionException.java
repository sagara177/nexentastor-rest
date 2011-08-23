package rest.client.exception;

public class RestClientConnectionException extends RestClientException {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public RestClientConnectionException(Throwable t) {
        super(t);
    }

    public RestClientConnectionException(String message, Throwable t) {
        super(message, t);
    }

}
