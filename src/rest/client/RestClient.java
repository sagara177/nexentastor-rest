package rest.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Properties;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import rest.client.exception.RestClientCommandException;
import rest.client.exception.RestClientConnectionException;
import rest.client.exception.RestClientHttpStatusException;
import rest.client.exception.RestClientJsonFormatException;
import rest.nexentastor.JsonRequest;
import rest.nexentastor.JsonResponse;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

/**
 * NexentaStor RESTful client.
 * 
 * <p>
 * RestClient post JsonRequest object, and get JsonResponse object.
 * </p>
 * 
 * <p>
 * RestClient has some expensive properties to initialize. so limited
 * construction, and provides singleton access.
 * </p>
 * 
 * @author sagara
 * 
 */
public class RestClient {
    private static Logger logger = Logger.getLogger(RestClient.class);

    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "2000";
    private static final String DEFAULT_USER = "";
    private static final String DEFAULT_PASS = "";

    private static final String DEFAULT_CONNECT_TIMEOUT = "60000"; // 60 * 1000;
    private static final String DEFAULT_READ_TIMEOUT = "60000"; // 60 * 1000;
    private static final String DEFAULT_THREADPOOL_SIZE = "50";

    private static final String PATH = "/rest/nms/";

    private Properties props = null;
    private String uri = null;

    // com.sun.jersey.api.client.Client has multi-threaded feature
    // in it's inner, and it's expensive to initialize.
    // should not create multiple object.
    private Client client = null;
    private WebResource resource = null;

    @SuppressWarnings("unused")
    private static final LinkedHashMap<String, Object> EMPTY_MAP = new LinkedHashMap<String, Object>();

    // singleton
    private static RestClient instance = null;

    private RestClient(Properties props) throws URISyntaxException {
        this.props = props;

        String user = props.getProperty("user", DEFAULT_USER);
        String pass = props.getProperty("password", DEFAULT_PASS);
        int connectTimeout = Integer.parseInt(props.getProperty(
                "connecttimeout", DEFAULT_CONNECT_TIMEOUT));
        int readTimeout = Integer.parseInt(props.getProperty("commandtimeout",
                DEFAULT_READ_TIMEOUT));
        int threads = Integer.parseInt(props.getProperty("maxconnections",
                DEFAULT_THREADPOOL_SIZE));

        // prepare rest client with HTTP basic authentication
        ClientConfig cc = new DefaultClientConfig();
        cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
        // connect timeout in milliseconds
        cc.getProperties().put(ClientConfig.PROPERTY_CONNECT_TIMEOUT,
                connectTimeout);
        // read timeout in milliseconds
        cc.getProperties().put(ClientConfig.PROPERTY_READ_TIMEOUT, readTimeout);
        cc.getProperties().put(ClientConfig.PROPERTY_THREADPOOL_SIZE, threads);

        client = Client.create(cc);
        client.addFilter(new HTTPBasicAuthFilter(user, pass));
        uri = getUri();
        resource = client.resource(uri);
    }

    private String getUri() throws URISyntaxException {
        String host = props.getProperty("host", DEFAULT_HOST);
        int port = Integer.parseInt(props.getProperty("port", DEFAULT_PORT));
        URI uri = new URI("http", null, host, port, PATH, null, null);
        return uri.toString();
    }

    /**
     * get RestClient singleton object.
     * 
     * @param props
     *            connection properties to rest server info
     * @throws URISyntaxException
     */
    public static synchronized RestClient getInstance(Properties props)
            throws URISyntaxException {
        if (instance == null) {
            instance = new RestClient(props);
        }
        return instance;
    }

    /**
     * Connecting to server, and HTTP-post JSON data.
     * 
     * If response is none (SA-API's return value type is 'void'), returns empty
     * string ("").
     * 
     * @param request
     *            NexentaStor RESTful API formatted JSON data.
     * @return JSON response data.
     * @throws RestClientConnectionException
     *             if connecting to server fails.
     * @throws RestClientHttpStatusException
     *             if response HTTP status is not '2??'.
     * @throws RestClientJsonFormatException
     *             if response JSON format is illegal.
     * @throws RestClientCommandException
     *             if response JSON contains error field.
     */
    public <T> JsonResponse<T> doRequest(JsonRequest request)
            throws RestClientConnectionException,
            RestClientHttpStatusException, RestClientJsonFormatException,
            RestClientCommandException {
        ClientResponse res = null;
        if (request == null || request.equals("")) {
            throw new RestClientJsonFormatException("null request.");
        }

        // start HTTP connection
        try {
            logger.debug("connect: " + uri + ", request: " + request);
            res = resource.accept(MediaType.APPLICATION_JSON_TYPE)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .post(ClientResponse.class, request.toString());
        } catch (Exception e) {
            throw new RestClientConnectionException(
                    "failed to connect: " + uri, e);
        } finally {

        }

        // check HTTP status code
        int status = res.getStatus();
        String entity = res.getEntity(String.class);
        if (status / 100 != 2) {
            throw new RestClientHttpStatusException(status, entity);
        }

        // parse JSON response
        JsonResponse<T> response = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            response = mapper.readValue(entity,
                    new TypeReference<JsonResponse<T>>() {
                    });
            response.setHttpStatus(status);
            response.setHttpEntity(entity);
        } catch (Exception e) {
            throw new RestClientJsonFormatException(
                    "failed to parse JSON response: " + entity, e);
        }

        // check error field in JSON response.
        errorCheck(request, response);
        return response;
    }

    private void errorCheck(JsonRequest request, JsonResponse<?> response)
            throws RestClientCommandException {
        if (response == null || response.equals(""))
            return;
        JsonResponse.Error error = response.getError();
        if (error == null)
            return;
        String message = error.getMessage();
        if (message == null || message.equals(""))
            return;

        throw new RestClientCommandException(request, response);
    }
}
