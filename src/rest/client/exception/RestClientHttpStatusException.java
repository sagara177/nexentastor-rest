package rest.client.exception;

public class RestClientHttpStatusException extends RestClientException {
    private int status;
    private String entity;

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public RestClientHttpStatusException(int status, String entity) {
        super("status: " + status + ", response entity: " + entity);
        this.status = status;
        this.entity = entity;
    }

    public int getStatus() {
        return status;
    }

    public String getEntity() {
        return entity;
    }
}
