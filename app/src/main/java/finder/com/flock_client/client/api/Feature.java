package finder.com.flock_client.client.api;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Daniel on 18/6/2016.
 */

public class Feature {

    private LatLng pos;
    private String placeId;
    private ArrayList<String> types = new ArrayList<>();
    private String name;
    private URL iconUrl;

    public Feature(String placeId, String name, double lat, double lng, ArrayList<String> types, String iconUrl) {
        this.placeId = placeId;
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
}
