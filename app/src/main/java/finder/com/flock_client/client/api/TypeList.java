package finder.com.flock_client.client.api;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Daniel on 10/6/16.
 */

public class TypeList {

    private HttpClient client;

    public TypeList(String token) {
        client = new HttpClient(token);
    }

    public JSONArray getTypes() throws Exception {
        JSONObject resp = client.makeGetRequest("/v0/api/types");
        if (resp.getBoolean("success")) {
            return resp.getJSONArray("data");
        }
        return null;
    }
}
