package rest.nexentastor.api;

import java.util.ArrayList;

import rest.nexentastor.JsonRequest;


public class Stmf extends JsonRequest {
	protected static final String OBJECT_NAME = "stmf";

	protected Stmf() {
		super();
	}

	protected Stmf(String object, ArrayList<Object> params, String method) {
		super(object, params, method);
	}
	
	public static Stmf create_targetgroup(String tg_name) {
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(tg_name);
		return new Stmf(OBJECT_NAME, params, "create_targetgroup");
	}
	
	public static Stmf destroy_targetgroup(String tg_name) {
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(tg_name);
		return new Stmf(OBJECT_NAME, params, "destroy_targetgroup");
	}
	
	public static Stmf resolve_lun_to_targets(String zvol_name) {
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(zvol_name);
		return new Stmf(OBJECT_NAME, params, "resolve_lun_to_targets");
	}
	
	public static Stmf add_targetgroup_member(String tg_name, String tg_member_name) {
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(tg_name);
		params.add(tg_member_name);
		return new Stmf(OBJECT_NAME, params, "add_targetgroup_member");
	}
	
	public static Stmf remove_targetgroup_member(String tg_name, String tg_member_name) {
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(tg_name);
		params.add(tg_member_name);
		return new Stmf(OBJECT_NAME, params, "remove_targetgroup_member");
	}

}
