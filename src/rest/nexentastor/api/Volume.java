package rest.nexentastor.api;

import java.util.ArrayList;

import rest.nexentastor.JsonRequest;

public class Volume extends JsonRequest {
	protected static final String OBJECT_NAME = "volume";
	
	public static final String FREE = "free";
	
	protected Volume() {
		super();
	}
	protected Volume(String object, ArrayList<Object> params, String method) {
		super(object, params, method);
	}
	
	public static Volume get_child_prop(String child_name, String propname) {
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(child_name);
		params.add(propname);
		return new Volume(OBJECT_NAME, params, "get_child_prop");
	}

}
