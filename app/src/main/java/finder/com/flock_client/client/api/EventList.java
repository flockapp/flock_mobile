package finder.com.flock_client.client.api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Daniel on 6/6/16.
 */

public class EventList {
    private ArrayList<Event> events = new ArrayList<>();
    private HttpClient client;

    public EventList(String token) {
        client = new HttpClient(token);
    }

    public void retrieveEvents() throws Exception {
        JSONObject resp = client.makeGetRequest("/v0/api/events");
        if (resp.getBoolean("success")) {
            JSONArray eventList = resp.getJSONArray("data");
            for (int i = 0; i < eventList.length(); i++) {
                JSONObject event = eventList.getJSONObject(i);
                this.events.add(new Event(
                        event.getInt("id"),
                        client.getToken(),
                        event.getString("name"),
                        event.getLong("time"),
                        event.getDouble("lat"),
                        event.getDouble("lng")
                ));
            }
        } else {
            throw new Exception(resp.getString("message"));
        }
    }

    public ArrayList<Event> getEventList() {
        return events;
    }

}
