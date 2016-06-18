package finder.com.flock_client.client.api;

import java.util.ArrayList;

/**
 * Created by Daniel on 18/6/2016.
 */

public class Feature {

    private double lat, lng;
    private String placeId;
    private ArrayList<String> types = new ArrayList<>();
    private String name;
    private String iconUrl;

    public Feature(String placeId, String name, double lat, double lng, ArrayList<String> types, String iconUrl) {
        this.placeId = placeId;
        this.name = name;
        this.types = types;
        this.iconUrl = iconUrl;
        this.lat = lat;
        this.lng = lng;
    }


}
