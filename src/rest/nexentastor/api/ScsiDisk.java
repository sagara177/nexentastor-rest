package rest.nexentastor.api;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import rest.nexentastor.JsonRequest;



public class ScsiDisk extends JsonRequest {
	protected static final String OBJECT_NAME = "scsidisk";
	
	public static final String INITIATOR_GROUP_KEY = "host_group";
	public static final String TARGET_GROUP_KEY = "target_group";
	public static final String ALL_GROUP = "All";
	
	protected ScsiDisk() {
		super();
	}
	
	protected ScsiDisk(String object, ArrayList<Object> params, String method) {
		super(object, params, method);
	}
	
	public static ScsiDisk create_lu(String zvol_name, LinkedHashMap<String, Object> zvol_props) {
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(zvol_name);
		params.add(zvol_props);
		return new ScsiDisk(OBJECT_NAME, params, "create_lu");
	}
	
	public static ScsiDisk delete_lu(String zvol_name) {
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(zvol_name);
		return new ScsiDisk(OBJECT_NAME, params, "delete_lu");
	}
	
	public static ScsiDisk add_lun_mapping_entry(String zvol_name, LinkedHashMap<String, Object> map_entry) {
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(zvol_name);
		params.add(map_entry);
		return new ScsiDisk(OBJECT_NAME, params, "add_lun_mapping_entry");
	}
	
	public static ScsiDisk remove_lun_mapping_entry(String zvol_name, String map_entry_no) {
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(zvol_name);
		params.add(map_entry_no);
		return new ScsiDisk(OBJECT_NAME, params, "remove_lun_mapping_entry");
	}
	
	public static ScsiDisk list_lun_mapping_entries(String zvol_name) {
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(zvol_name);
		return new ScsiDisk(OBJECT_NAME, params, "list_lun_mapping_entries");
	}
	
	public static ScsiDisk resolve_lun_to_targets(String zvol_name) {
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(zvol_name);
		return new ScsiDisk(OBJECT_NAME, params, "resolve_lun_to_targets");
	}

}
