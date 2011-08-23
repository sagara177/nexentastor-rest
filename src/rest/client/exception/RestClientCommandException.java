package rest.client.exception;

import rest.nexentastor.JsonRequest;
import rest.nexentastor.JsonResponse;

public class RestClientCommandException extends RestClientException {
    JsonRequest request;

    JsonResponse<?> response;

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public RestClientCommandException(Throwable t) {
        super(t);
    }

    public RestClientCommandException(JsonRequest request,
            JsonResponse<?> response) {
        super(response.getError().getMessage() + ", request: " + request);
        this.request = request;
        this.response = response;
    }

    public JsonResponse<?> getResponse() {
        return response;
    }

}
