package sample;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.UUID;

import rest.client.RestClient;
import rest.client.exception.RestClientCommandException;
import rest.client.exception.RestClientConnectionException;
import rest.client.exception.RestClientHttpStatusException;
import rest.client.exception.RestClientJsonFormatException;
import rest.nexentastor.JsonRequest;
import rest.nexentastor.JsonResponse;
import rest.nexentastor.api.IscsiTarget;
import rest.nexentastor.api.ScsiDisk;
import rest.nexentastor.api.Stmf;
import rest.nexentastor.api.Volume;
import rest.nexentastor.api.Zvol;

/**
 * NexentaStor API client.
 * 
 * <p>
 * This class do NexentaStor API request. and provide high level API, contains
 * volume operation logic. Client post JsonRequest object's strings, and parse
 * response JSON string to JsonResponse object.
 * </p>
 * 
 * @author sagara
 * 
 */
public class NexentaStorClient {
    @SuppressWarnings("unused")
    private Properties withProps = null;
    private RestClient client = null;

    private LinkedHashMap<String, Object> zvolProps = null;
    private LinkedHashMap<String, Object> authProps = null;

    private String zvolBlocksize = null;
    private boolean zvolSparse = false;

    private static final String DEFAULT_USER = "admin";
    private static final String DEFAULT_PASS = "nexenta";

    private static final String DEFAULT_BLOCKSIZE = "8KB";
    private static final String DEFAULT_SPARSE = "0";
    private static final String DEFAULT_PRIMARYCACHE = "all";
    private static final String DEFAULT_SECONDARYCACHE = "all";
    private static final String DEFAULT_READONLY = "off";
    private static final String DEFAULT_COMPRESSION = "off";
    private static final String DEFAULT_CHECKSUM = "on";
    private static final String DEFAULT_DEDUP = "off";
    private static final String DEFAULT_LOGBIAS = "latency";
    private static final String DEFAULT_COPIES = "1";
    private static final String DEFAULT_SYNC = "standard";

    private static final String DEFAULT_AUTH_METHOD = IscsiTarget.AUTH_METHOD_NONE;
    private static final String DEFAULT_CHAP_USER = "";
    private static final String DEFAULT_CHAP_PASS = "";

    private static final String TARGETGROUP_PREFIX = "tg_";
    private static final LinkedHashMap<String, Object> EMPTY_MAP = new LinkedHashMap<String, Object>();

    private static final Object LOCK = new Object();

    /**
     * NexentaStorClient constructor.
     * 
     * @param withProps
     *            properties to connect nexentastor nmv
     */
    public NexentaStorClient(Properties withProps) {
        withProps.setProperty("user",
                withProps.getProperty("user", DEFAULT_USER));
        withProps.getProperty("password",
                withProps.getProperty("password", DEFAULT_PASS));
        this.withProps = withProps;

        zvolProps = new LinkedHashMap<String, Object>();
        zvolProps.put("primarycache", withProps.getProperty(
                "nexentastor.zvol.primarycache", DEFAULT_PRIMARYCACHE));
        zvolProps.put("secondarycache", withProps.getProperty(
                "nexentastor.zvol.secondarycache", DEFAULT_SECONDARYCACHE));
        zvolProps.put("readonly", withProps.getProperty(
                "nexentastor.zvol.readonly", DEFAULT_READONLY));
        zvolProps.put("compression", withProps.getProperty(
                "nexentastor.zvol.compression", DEFAULT_COMPRESSION));
        zvolProps.put("checksum", withProps.getProperty(
                "nexentastor.zvol.checksum", DEFAULT_CHECKSUM));
        zvolProps.put("dedup",
                withProps.getProperty("nexentastor.zvol.dedup", DEFAULT_DEDUP));
        zvolProps.put("logbias", withProps.getProperty(
                "nexentastor.zvol.logbias", DEFAULT_LOGBIAS));
        zvolProps.put("copies", withProps.getProperty(
                "nexentastor.zvol.copies", DEFAULT_COPIES));
        zvolProps.put("sync",
                withProps.getProperty("nexentastor.zvol.sync", DEFAULT_SYNC));

        authProps = new LinkedHashMap<String, Object>();
        authProps.put("auth", withProps.getProperty(
                "nexentastor.iscsitarget.auth", DEFAULT_AUTH_METHOD));
        authProps.put("chap_user", withProps
                .getProperty("nexentastor.iscsitarget.chap_user",
                        DEFAULT_CHAP_USER));
        authProps.put("chap_secret", withProps
                .getProperty("nexentastor.iscsitarget.chap_secret",
                        DEFAULT_CHAP_PASS));

        zvolBlocksize = withProps.getProperty("nexentastor.zvol.blocksize", DEFAULT_BLOCKSIZE);
        zvolSparse = withProps.getProperty("nexentastor.zvol.sparse", DEFAULT_SPARSE).equals("1");

        try {
            // this.client = new RestClient(withProps);
            this.client = RestClient.getInstance(withProps);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create zvol volume with iSCSI target. This method do following steps.
     * 
     * <ol>
     * <li>create zvol, and set chap authentication</li>
     * <li>create logical unit for zvol</li>
     * <li>create iSCSI target</li>
     * <li>create target group</li>
     * <li>add iSCSI target to target group</li>
     * <li>add mapping initiator to target group for logical unit</li>
     * </ol>
     * 
     * All steps success, return new zvol IQN.
     * 
     * @param zvol_name
     *            new zvol name.
     * @param size
     *            volume size in GB unit.
     * @param props
     *            option properties for creating volume.
     * @return new zvol IQN
     * @throws RestClientConnectionException
     *             if connecting to server fails.
     * @throws RestClientHttpStatusException
     *             if response HTTP status is not '2??'.
     * @throws RestClientJsonFormatException
     *             if response JSON format is illegal.
     * @throws RestClientCommandException
     *             if response JSON contains error field.
     */
    public String createVolume(String zvol_name, int size,
            LinkedHashMap<String, Object> props)
            throws RestClientConnectionException,
            RestClientHttpStatusException, RestClientJsonFormatException,
            RestClientCommandException {
        String tg_member_name = null;
        JsonRequest req = null;
        String tg_name = TARGETGROUP_PREFIX + zvol_name;
        // 1. create zvol, and set chap authentication
        req = Zvol.create_with_props(zvol_name, size + Zvol.GB,
                zvolBlocksize, zvolSparse, props);
        client.doRequest(req);
        // 2. create logical unit for zvol
        req = ScsiDisk.create_lu(zvol_name, EMPTY_MAP);
        client.doRequest(req);
        // 3. create iSCSI target
        // req = IscsiTarget.create_target(EMPTY_MAP);
        req = IscsiTarget.create_target(authProps);
        JsonResponse<String> res = client.doRequest(req);
        tg_member_name = res.getResult();
        // 4. create target group
        req = Stmf.create_targetgroup(tg_name);
        client.doRequest(req);
        // 5. add iSCSI target to target group
        req = Stmf.add_targetgroup_member(tg_name, tg_member_name);
        client.doRequest(req);
        // 6. add mapping initiator to target group for logical unit
        LinkedHashMap<String, Object> map_entry = new LinkedHashMap<String, Object>();
        map_entry.put(ScsiDisk.INITIATOR_GROUP_KEY, ScsiDisk.ALL_GROUP);
        map_entry.put(ScsiDisk.TARGET_GROUP_KEY, tg_name);
        req = ScsiDisk.add_lun_mapping_entry(zvol_name, map_entry);
        client.doRequest(req);
        return tg_member_name;
    }

    /**
     * Clone zvol volume with new iSCSI target. This method do following steps.
     * 
     * <ol>
     * <li>create zvol snapshot</li>
     * <li>clone zvol from snapshot</li>
     * <li>create logical unit for zvol</li>
     * <li>create iSCSI target</li>
     * <li>create target group</li>
     * <li>add iSCSI target to target group</li>
     * <li>add mapping initiator to target group for logical unit</li>
     * </ol>
     * 
     * All steps success, return new zvol IQN.
     * 
     * @param orig_zname
     *            original zvol name
     * @param new_zname
     *            new zvol name
     * @return new zvol IQN
     * @throws RestClientConnectionException
     *             if connecting to server fails.
     * @throws RestClientHttpStatusException
     *             if response HTTP status is not '2??'.
     * @throws RestClientJsonFormatException
     *             if response JSON format is illegal.
     * @throws RestClientCommandException
     *             if response JSON contains error field.
     */
    public String cloneVolume(String orig_zname, String new_zname)
            throws RestClientConnectionException,
            RestClientHttpStatusException, RestClientJsonFormatException,
            RestClientCommandException {
        String tg_member_name = null;
        JsonRequest req = null;
        String snapname = null;
        synchronized (LOCK) {
            snapname = UUID.randomUUID().toString();
        }
        String tg_name = TARGETGROUP_PREFIX + new_zname;
        // 1. create zvol snapshot
        req = Zvol.create_snapshot(orig_zname, snapname, false);
        client.doRequest(req);
        // 2. clone zvol from snapshot
        req = Zvol.clone(orig_zname + "@" + snapname, new_zname);
        client.doRequest(req);
        // 3. create logical unit for zvol
        req = ScsiDisk.create_lu(new_zname, EMPTY_MAP);
        client.doRequest(req);
        // 4. create iSCSI target
        // req = IscsiTarget.create_target(EMPTY_MAP);
        req = IscsiTarget.create_target(authProps);
        JsonResponse<String> res = client.doRequest(req);
        tg_member_name = res.getResult();
        // 5. create target group
        req = Stmf.create_targetgroup(tg_name);
        client.doRequest(req);
        // 6. add iSCSI target to target group
        req = Stmf.add_targetgroup_member(tg_name, tg_member_name);
        client.doRequest(req);
        // 7. add mapping initiator to target group for logical unit
        LinkedHashMap<String, Object> map_entry = new LinkedHashMap<String, Object>();
        map_entry.put(ScsiDisk.INITIATOR_GROUP_KEY, ScsiDisk.ALL_GROUP);
        map_entry.put(ScsiDisk.TARGET_GROUP_KEY, tg_name);
        req = ScsiDisk.add_lun_mapping_entry(new_zname, map_entry);
        client.doRequest(req);
        return tg_member_name;
    }

    /**
     * Delete zvol volume and iSCSI target. This method do following steps.
     * 
     * <ol>
     * <li>prepare target name to delete zvol. each zvol should have just one
     * target</li>
     * <li>remove mapping initiator to target group for logical unit. 'mapping
     * entry number' is always "0"</li>
     * <li>remove iSCSI target to target group</li>
     * <li>destroy target group</li>
     * <li>delete iSCSI target</li>
     * <li>delete logical unit for zvol</li>
     * <li>destroy zvol</li>
     * </ol>
     * 
     * If deleting was failed, throw Exception.
     * 
     * @param zvol_name
     *            delete target zvol name
     * @throws RestClientConnectionException
     *             if connecting to server fails.
     * @throws RestClientHttpStatusException
     *             if response HTTP status is not '2??'.
     * @throws RestClientJsonFormatException
     *             if response JSON format is illegal.
     * @throws RestClientCommandException
     *             if response JSON contains error field.
     */
    public void deleteVolume(String zvol_name)
            throws RestClientConnectionException,
            RestClientHttpStatusException, RestClientJsonFormatException,
            RestClientCommandException {
        JsonRequest req = null;
        String tg_name = TARGETGROUP_PREFIX + zvol_name;
        // 1. prepare target name to delete zvol.
        // each zvol should have just one target
        req = ScsiDisk.resolve_lun_to_targets(zvol_name);
        JsonResponse<ArrayList<String>> res = client.doRequest(req);
        ArrayList<String> targetList = res.getResult();
        String tg_member_name = targetList.get(0);
        // 2. remove mapping initiator to target group for logical unit
        // 'mapping entry number' is always "0"
        req = ScsiDisk.remove_lun_mapping_entry(zvol_name, "0");
        client.doRequest(req);
        // 3. remove iSCSI target to target group
        req = Stmf.remove_targetgroup_member(tg_name, tg_member_name);
        client.doRequest(req);
        // 4. destroy target group
        req = Stmf.destroy_targetgroup(tg_name);
        client.doRequest(req);
        // 5. delete iSCSI target
        req = IscsiTarget.delete_target(tg_member_name);
        client.doRequest(req);
        // 6. delete logical unit for zvol
        req = ScsiDisk.delete_lu(zvol_name);
        client.doRequest(req);
        // 7. destroy zvol
        req = Zvol.destroy(zvol_name, Zvol.DESTROY_RECURSIVE);
        client.doRequest(req);
    }

    /**
     * Check if target zvol is exists. This method do following steps.
     * 
     * <ol>
     * <li>verify zvol exists</li>
     * </ol>
     * 
     * @return true if exists, or false
     * @throws RestClientConnectionException
     *             if connecting to server fails.
     * @throws RestClientHttpStatusException
     *             if response HTTP status is not '2??'.
     * @throws RestClientJsonFormatException
     *             if response JSON format is illegal.
     * @throws RestClientCommandException
     *             if response JSON contains error field.
     */
    public boolean checkVolume(String zvol_name)
            throws RestClientConnectionException,
            RestClientHttpStatusException, RestClientJsonFormatException,
            RestClientCommandException {
        JsonRequest req = null;
        // 1. verify zvol exists
        req = Zvol.object_exists(zvol_name);
        JsonResponse<Integer> res = client.doRequest(req);
        return res.getResult() == 1;
    }

    /**
     * List all zvol volume. This method do following steps.
     * 
     * <ol>
     * <li>get zvol names</li>
     * </ol>
     * 
     * @param zvol_name_pattern
     *            name pattern in regexp
     * @return zvol name list
     * @throws RestClientConnectionException
     *             if connecting to server fails.
     * @throws RestClientHttpStatusException
     *             if response HTTP status is not '2??'.
     * @throws RestClientJsonFormatException
     *             if response JSON format is illegal.
     * @throws RestClientCommandException
     *             if response JSON contains error field.
     */
    public ArrayList<String> listVolumes(String zvol_name_pattern)
            throws RestClientConnectionException,
            RestClientHttpStatusException, RestClientJsonFormatException,
            RestClientCommandException {
        JsonRequest req = null;
        // 1. get zvol names
        req = Zvol.get_names(zvol_name_pattern);
        JsonResponse<ArrayList<String>> res = client.doRequest(req);
        return res.getResult();
    }

    /**
     * Get free size in pool. This method do following steps.
     * 
     * <ol>
     * <li>get free size in pool</li>
     * </ol>
     * 
     * @param pool
     *            target pool
     * @return free size with unit
     * @throws RestClientConnectionException
     *             if connecting to server fails.
     * @throws RestClientHttpStatusException
     *             if response HTTP status is not '2??'.
     * @throws RestClientJsonFormatException
     *             if response JSON format is illegal.
     * @throws RestClientCommandException
     *             if response JSON contains error field.
     */
    public String getFree(String pool) throws RestClientConnectionException,
            RestClientHttpStatusException, RestClientJsonFormatException,
            RestClientCommandException {
        JsonRequest req = null;
        // 1. get free size in pool
        req = Volume.get_child_prop(pool, Volume.FREE);
        JsonResponse<String> res = client.doRequest(req);
        return res.getResult();
    }

    /**
     * @return the zvolProps
     */
    public LinkedHashMap<String, Object> getZvolProps() {
        return zvolProps;
    }

    /**
     * @param zvolProps
     *            the zvolProps to set
     */
    public void setZvolProps(LinkedHashMap<String, Object> zvolProps) {
        this.zvolProps = zvolProps;
    }
}
