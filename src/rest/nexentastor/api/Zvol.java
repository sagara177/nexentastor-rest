package rest.nexentastor.api;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import rest.nexentastor.JsonRequest;



public class Zvol extends JsonRequest {
	protected static final String OBJECT_NAME = "zvol";

	public static final String GB = "GB";
	public static final String DEFAULT_BLOCKSIZE = "8KB";
	public static final String DESTROY_RECURSIVE = "-r";
	
	protected Zvol() {
		super();
	}
	protected Zvol(String object, ArrayList<Object> params, String method) {
		super(object, params, method);
	}
	
	public static Zvol create_with_props(String zname, String devsize, String blocksize,
			Boolean sparse, LinkedHashMap<String, Object> creation_time_props) {
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(zname);
		params.add(devsize);
		params.add(blocksize);
		params.add(sparse);
		params.add(creation_time_props);
		return new Zvol(OBJECT_NAME, params, "create_with_props");
	}
	
	public static Zvol create_snapshot(String zname, String snapname, Boolean recursive) {
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(zname);
		params.add(snapname);
		params.add(recursive);
		return new Zvol(OBJECT_NAME, params, "create_snapshot");
	}
	
	public static Zvol clone(String zname_snapname, String target_zname) {
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(zname_snapname);
		params.add(target_zname);
		return new Zvol(OBJECT_NAME, params, "clone");
	}
	
	public static Zvol destroy(String zname, String extra_options) {
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(zname);
		params.add(extra_options);
		return new Zvol(OBJECT_NAME, params, "destroy");
	}

	public static Zvol object_exists(String child_name) {
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(child_name);
		return new Zvol(OBJECT_NAME, params, "object_exists");
	}
	
	public static Zvol get_names(String pattern) {
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(pattern);
		return new Zvol(OBJECT_NAME, params, "get_names");
	}
	
	public static Zvol get_names_by_prop(String propname, String propval_pattern, String childname_pattern) {
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(propname);
		params.add(propval_pattern);
		params.add(childname_pattern);
		return new Zvol(OBJECT_NAME, params, "get_names_by_prop");
	}
	
}
