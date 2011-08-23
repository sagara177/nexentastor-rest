package rest.nexentastor;

import java.util.ArrayList;

/**
 * <p>
 * This class provides NexentaStor RESTful API's request format in JSON. see
 * NexentaStor REST API document[1].
 * </p>
 * 
 * <p>
 * [1] 'REST-API User Guide v1.0 - Nexenta',
 *     <a href="http://www.nexenta.com/corp/static/docs-stable/NexentaStor-RESTful-API.pdf">
 *     http://www.nexenta.com/corp/static/docs-stable/NexentaStor-RESTful-API.pdf</a>
 * </p>
 * 
 * @author sagara
 * 
 */
public class JsonRequest extends JsonData {
    String object;
    ArrayList<Object> params;
    String method;

    public JsonRequest() {
    }

    public JsonRequest(String object, ArrayList<Object> params, String method) {
        this.object = object;
        this.params = params;
        this.method = method;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public ArrayList<Object> getParams() {
        return params;
    }

    public void setParams(ArrayList<Object> params) {
        this.params = params;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

}
