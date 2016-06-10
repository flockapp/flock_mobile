package finder.com.flock_client.client.api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Daniel on 6/6/16.
 */

public class Event {

    private int id;
    private HttpClient client;

    private double lat, lng;
    private String name;
    private int cost;
    private long time;
    private Integer[] types;

    public Event(String token, String name, long time, int cost, Integer[] types, double lat, double lng) {
        this.client = new HttpClient(token);
        this.name = name;
        this.cost = cost;
        this.time = time;
        this.types = types;
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
        dataObj.put("cost", cost);
        dataObj.put("time", time);
        dataObj.put("lat", lat);
        dataObj.put("types", new JSONArray(types));
        dataObj.put("lng", lng);
        return client.makePostRequest("/v0/api/events", dataObj);
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        Date date = new Date(this.time*1000L); //Convert to ms;
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }

    public String getTime() {
        Date date = new Date(this.time*1000L); //Convert to ms;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mma");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }
}
