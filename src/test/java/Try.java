import io.vertx.core.json.JsonObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by huiyu on 17/3/13.
 */
public class Try {
    public static void main(String[] args) {
        JsonObject jsonObject = new JsonObject("{\"hosts\":[{\"hostName\":\"node-4.domain.tld\"}," +
                "{\"hostName\":\"node-5.domain.tld\"},{\"hostName\":\"node-6.domain.tld\"}]}");
        jsonObject.getJsonArray("hosts").forEach(e -> {
            String host = new JsonObject(e.toString()).getString("hostName");
            System.out.println(host);
        });

        Set<String> set = new HashSet<>();
        set.add(new String("123"));
        set.remove(new String("123"));
//        System.out.println(set.size());
        ArrayList<String> list = new ArrayList<>();
        list.add("123");
        list.add("234");
        JsonObject jas = new JsonObject().put("a", list);
        System.out.println(jas);


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now.format(formatter));

        String tableName = "fact_vpn_traffic_1h";
        if (tableName.startsWith("fact_vnf") || tableName.startsWith("dim_domain") || tableName.startsWith
                ("fact_fw_netflow") || tableName.startsWith("fact_dms_server")) {
            System.out.println("True");
        }

    }
}
