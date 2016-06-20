package finder.com.flock_client.client.api;

import android.util.Log;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import finder.com.flock_client.AppConfig;
import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Created by Daniel on 18/6/2016.
 */

public class Feature {

    private HttpClient client;
    private LatLng pos;
    private String placeId;
    private ArrayList<String> types = new ArrayList<>();
    private String name;
    private URL iconUrl;

    private int cost;
    private double rating;
    private String address;
    private URL websiteUrl;
    ArrayList<Pair<Integer, Integer>> timePeriods = new ArrayList<>();

    public Feature(String placeId) {
        client = new HttpClient();
        this.placeId = placeId;
    }

    public Feature(String placeId, String name, double lat, double lng, ArrayList<String> types, String iconUrl) {
        this(placeId);
        this.name = name;
        this.types = types;
        try {
            this.iconUrl = new URL(iconUrl);
        } catch (MalformedURLException e) {
            Log.d("debug url", "error", e);
        }
        this.pos = new LatLng(lat, lng);
    }

    public URL getIconUrl() {
        return iconUrl;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    public LatLng getPos() {
        return pos;
    }

    public int getCost() {
        return cost;
    }

    public String getAddress() {
        return address;
    }

    public double getRating() {
        return rating;
    }

    public URL getWebsiteUrl() {
        return websiteUrl;
    }

    public boolean populateDetails() {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("maps.googleapis.com")
                .addPathSegments("maps/api/place/details/json")
                .addQueryParameter("key", AppConfig.GOOGLE_API_KEY)
                .addQueryParameter("placeid", placeId)
                .build();
        Request.Builder reqBuilder = new Request.Builder()
                .url(url)
                .get();
        try {
            Log.d("debug", "populateDetails: entered");
            JSONObject resp = client.makeRequest(reqBuilder);
            if (resp.getString("status").equals("OK")) {
                JSONObject result = resp.getJSONObject("result");
                if (result.has("price_level"))
                    cost = result.getInt("price_level");
                if (result.has("rating"))
                    rating = result.getDouble("rating");
                if (result.has("website") && !result.getString("website").equals("")) {
                    websiteUrl = new URL(result.getString("website"));
                }
                if (result.has("formatted_address"))
                    address = result.getString("formatted_address");
                if (result.has("opening_hours")) {
                    JSONObject opening_hours = result.getJSONObject("opening_hours");
                    JSONArray periods = opening_hours.getJSONArray("periods");
                    for (int i = 0; i < periods.length(); i++) {
                        JSONObject open = periods.getJSONObject(i).getJSONObject("open");
                        JSONObject close = periods.getJSONObject(i).getJSONObject("close");
                        timePeriods.add(new Pair<>(Integer.parseInt(open.getString("time")),
                                Integer.parseInt(close.getString("time"))));
                    }
                }
                return true;
            }
        } catch (Exception e) {
            Log.d("debug http", "error", e);
        }
        return false;
    }
}
