package rest.nexentastor;

/**
 * <p>JSON response beans.
 * properties details, see NexentaStor REST API document[1].</p>
 * 
 * <p>JsonResponse's type parameter is defined by SA-API[2].
 * datatype corresponding rule is defined in jackson library[3].</p>
 * 
 * <p>
 * [1] 'REST-API User Guide v1.0 - Nexenta',
 *     <a href="http://www.nexenta.com/corp/static/docs-stable/NexentaStor-RESTful-API.pdf">
 *     http://www.nexenta.com/corp/static/docs-stable/NexentaStor-RESTful-API.pdf</a><br>
 * [2] 'Storage Appliance API',
 *     <a href="http://www.nexenta.com/static/NexentaStor-API.html">
 *     http://www.nexenta.com/static/NexentaStor-API.html</a><br>
 * [3] 'JacksonInFiveMinutes - FasterXML Wiki',
 *     <a href="http://www.nexenta.com/static/NexentaStor-API.html">
 *     http://wiki.fasterxml.com/JacksonInFiveMinutes</a>
 * </p>
 * 
 * @author sagara
 *
 * @param <T> JSON response datatype.
 */
public class JsonResponse<T> extends JsonData {
    public static class Error {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    protected Object tg_flash;
    protected T result;
    protected Error error;

    protected int httpStatus;
    protected String httpEntity;

    public JsonResponse() {
    }

    public JsonResponse(Object tg_flash, T result, Error error) {
        this.tg_flash = tg_flash;
        this.result = result;
        this.error = error;
    }

    public Object getTg_flash() {
        return tg_flash;
    }

    public void setTg_flash(Object tgFlash) {
        tg_flash = tgFlash;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getHttpEntity() {
        return httpEntity;
    }

    public void setHttpEntity(String httpEntity) {
        this.httpEntity = httpEntity;
    }

}
