package finder.com.flock_client.client.api;

import org.json.JSONObject;

/**
 * Created by Daniel on 6/6/16.
 */

public class Event {

    private int id;
    private HttpClient client;

    private double lat, lng;
    private String name;
    private long time;

    public Event(String token, String name, long time, double lat, double lng) {
        this.client = new HttpClient(token);
        this.name = name;
        this.time = time;
        this.lat = lat;
        this.lng = lng;
    }

    public Event(int id, String token) {
        this.id = id;
        this.client = new HttpClient(token);
    }

    public Event(int id, String token, String name, long time, double lat, double lng) {
        this(id, token);
        this.name = name;
        this.time = time;
        this.lat = lat;
        this.lng = lng;
    }

    public JSONObject createEvent() throws Exception {
        JSONObject dataObj = new JSONObject();
        dataObj.put("name", name);
        dataObj.put("time", time);
        dataObj.put("lat", lat);
        dataObj.put("lng", lng);
        return client.makePostRequest("/v0/api/events", dataObj);
    }

    public String getName() {
        return name;
    }

    //TO-IMPLEMENT: getDate() and getTime()

    public String getDate() {
        return "";
    }

    public String getTime() {
        return "";
    }

}
