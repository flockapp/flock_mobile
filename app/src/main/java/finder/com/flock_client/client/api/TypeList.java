package finder.com.flock_client.client.api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Daniel on 10/6/16.
 */

public class TypeList {

    private HttpClient client;
    private ArrayList<String> typeList = new ArrayList<>();

    public TypeList(String token) {
        client = new HttpClient(token);
    }

    public void getTypes() throws Exception {
        typeList.clear();
        JSONObject resp = client.makeGetRequest("/v0/api/types");
        if (resp.getBoolean("success")) {
            JSONArray respData = resp.getJSONArray("data");
            for (int i = 0; i < respData.length(); i++) {
                JSONObject item = respData.getJSONObject(i);
                typeList.add(item.getString("name"));
            }
        }
    }

    public ArrayList<String> getTypeList() {
        return typeList;
    }
}
