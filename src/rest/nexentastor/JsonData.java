package rest.nexentastor;

import java.io.IOException;
import java.io.StringWriter;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * <p>
 * Basis of all JSON request and response classes.
 * </p>
 * 
 * <p>
 * This class provides data transfer feature from Java beans object to JSON
 * string representaion.
 * </p>
 * 
 * @author sagara
 * 
 */
public class JsonData {

    @Override
    public String toString() {
        StringWriter sw = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();
        SerializationConfig config = mapper.getSerializationConfig();
        config.setSerializationInclusion(Inclusion.NON_NULL);
        try {
            mapper.writeValue(sw, this);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sw.toString();
    }

}
