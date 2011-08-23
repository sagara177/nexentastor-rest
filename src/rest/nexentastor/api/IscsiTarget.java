package rest.nexentastor.api;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import rest.nexentastor.JsonRequest;


public class IscsiTarget extends JsonRequest {
	protected static final String OBJECT_NAME = "iscsitarget";
	
    public static final String AUTH_METHOD_NONE = "none";
    public static final String AUTH_METHOD_CHAP = "chap";

	protected IscsiTarget() {
		super();
	}

	protected IscsiTarget(String object, ArrayList<Object> params, String method) {
		super(object, params, method);
	}

	public static IscsiTarget create_target(LinkedHashMap<String, Object> target_props) {
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(target_props);
		return new IscsiTarget(OBJECT_NAME, params, "create_target");
	}

	public static IscsiTarget delete_target(String target_name) {
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(target_name);
		return new IscsiTarget(OBJECT_NAME, params, "delete_target");
	}

	public static IscsiTarget get_props(String pattern) {
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(pattern);
		return new IscsiTarget(OBJECT_NAME, params, "get_props");
	}

}
