package finder.com.flock_client.client.api;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import finder.com.flock_client.AppConfig;
import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Created by Daniel on 18/6/2016.
 */

public class FeatureList {

    private static final int RADIUS = 1500; //1.5 km

    private HttpClient client;

    public FeatureList() {
        this("");
    }

    public FeatureList(String token) {
        this.client = new HttpClient(token);
    }

    public ArrayList<Feature> getNearby(LatLng pos) {
        ArrayList<Feature> featureList = new ArrayList<>();

        //Build and conduct nearby search request
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("maps.googleapis.com")
                .addPathSegments("/maps/api/place/nearbysearch/json")
                .addQueryParameter("key", AppConfig.GOOGLE_API_KEY)
                .addQueryParameter("location", pos.latitude + "," + pos.longitude)
                .addQueryParameter("radius", RADIUS + "")
                .build();
        Request.Builder builder = new Request.Builder()
                .url(url)
                .get();
        try {
            JSONObject resp = client.makeRequest(builder);
            if (resp.getString("status").equals("OK")) {
                //Populate featureList with feature objects
                JSONArray results = resp.getJSONArray("results");
                for (int i = 0; i < results.length(); i++) {
                    JSONObject item = results.getJSONObject(i);
                    JSONObject location = item.getJSONObject("geometry").getJSONObject("location");

                    ArrayList<String> types = new ArrayList<>();
                    JSONArray jsonTypes = item.getJSONArray("types");
                    for (int j = 0; j < jsonTypes.length(); j++) {
                        types.add(jsonTypes.getString(j));
                    }
                    featureList.add(new Feature(
                        item.getString("place_id"),
                        item.getString("name"),
                        location.getDouble("lat"),
                        location.getDouble("lng"),
                        types,
                        item.getString("icon")
                    ));
                }
            }
            return featureList;
        } catch (Exception e) {
            Log.d("debug", "error", e);
        }
        return null;
    }

}
