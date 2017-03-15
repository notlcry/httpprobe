import io.vertx.core.json.JsonObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by huiyu on 17/3/13.
 */
public class Try {
    public static void main(String[] args) {
        JsonObject jsonObject = new JsonObject("{\"hosts\":[{\"hostName\":\"node-4.domain.tld\"}," +
                "{\"hostName\":\"node-5.domain.tld\"},{\"hostName\":\"node-6.domain.tld\"}]}");
        jsonObject.getJsonArray("hosts").forEach(e->{
            String host = new JsonObject(e.toString()).getString("hostName");
            System.out.println(host);
        });

        Set<String> set = new HashSet<>();
        set.add(new String("123"));
        set.remove(new String("123"));
        System.out.println(set.size());
    }
}
