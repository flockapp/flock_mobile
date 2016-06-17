package finder.com.flock_client.client.api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;

/**
 * Created by Daniel on 10/6/16.
 */

public class GuestList {
    private HttpClient client;
    private int eventId;
    private ArrayList<Guest> guests = new ArrayList<>();

    public GuestList(String token, int eventId) {
        client = new HttpClient(token);
        this.eventId = eventId;
    }

    public ArrayList<Guest> get() throws Exception {
        guests.clear();
        JSONObject resp = client.makeGetRequest("/v0/api/events/"+eventId+"/guests");
        if (resp.getBoolean("success")) {
            JSONArray respData = resp.getJSONArray("data");
            for (int i = 0; i < respData.length(); i++) {
                JSONObject item = respData.getJSONObject(i);
                guests.add(new Guest(
                        item.getString("fullName"),
                        item.getString("username")
                ));
            }
            return guests;
        }
        return null;
    }
}
