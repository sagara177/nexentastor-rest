package rest.client.exception;

public class RestClientJsonFormatException extends RestClientException {
    private String entity;

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public RestClientJsonFormatException() {
    }

    public RestClientJsonFormatException(String message) {
        super(message);
    }

    public RestClientJsonFormatException(Throwable t) {
        super(t);
    }

    public RestClientJsonFormatException(String message, Throwable t) {
        super(message, t);
    }

    public RestClientJsonFormatException(String message, Throwable t,
            String entity) {
        super(message, t);
        this.entity = entity;
    }

    public String getEntity() {
        return entity;
    }
}
