package finder.com.flock_client.client.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

import finder.com.flock_client.R;
import finder.com.flock_client.client.api.Event;
import finder.com.flock_client.client.api.Feature;
import finder.com.flock_client.client.api.FeatureList;

/**
 * Created by Daniel on 20/6/2016.
 */

public class SchedulerMainFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnCameraChangeListener,
        GoogleMap.OnMarkerClickListener, GoogleMap.InfoWindowAdapter {

    private Event event;
    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;
    private boolean tapped = false;
    private LatLng previousTarget = new LatLng(0, 0); //Default prev value
    private HashMap<Marker, Feature> markerFeaturesMap;

    public static SchedulerMainFragment newInstance() {
        return new SchedulerMainFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("debug", "WHAT THE FUCK");
        return inflater.inflate(R.layout.fragment_scheduler_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);
        FragmentManager fm = getChildFragmentManager();
        markerFeaturesMap = new HashMap<>();
        mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_event_scheduler);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_event_scheduler, mapFragment).commit();
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        Log.d("debug cameraZoom", cameraPosition.zoom + "");
        LatLng currentTarget = cameraPosition.target;
        //If marker was not tapped, zoom is beyond threshold and camera is moved (not zoomed)
        if (!tapped && cameraPosition.zoom > 13 && !previousTarget.equals(currentTarget)) {
            googleMap.clear(); //Removes all markers
            markerFeaturesMap.clear();
            previousTarget = currentTarget;
            new FetchFeaturesTask(cameraPosition.target).execute();
        }
        tapped = false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("debug", "onMapReady: called");
        this.googleMap = googleMap;
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnCameraChangeListener(this);
        googleMap.setInfoWindowAdapter(this);
        initMap();
    }

    public void setEvent(Event event) {
        this.event = event;
        Log.d("debug", "setEvent: called");
        initMap();
    }

    private void initMap() {
        if (googleMap == null) {
            mapFragment.getMapAsync(this);
        } else if (event != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(event.getLat(), event.getLng()), 14
            ));
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        tapped = true;
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                marker.getPosition(), 15
        ));
        new FetchFeatureDetailsTask(marker).execute();
        return true; //Disables default functionality
    }

    @Override
    public View getInfoWindow(Marker marker) {
        //Retrieve associated feature object
        Feature feature = markerFeaturesMap.get(marker);
        Log.d("debug", "getInfoWindow: called");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.infowindow_scheduler_main, null);
        TextView costTextView = (TextView) view.findViewById(R.id.text_info_window_cost);
        TextView nameTextView = (TextView) view.findViewById(R.id.text_info_window_name);
        TextView addressTextView = (TextView) view.findViewById(R.id.text_info_window_address);

        nameTextView.setText(String.valueOf(feature.getName()));
        costTextView.setText(String.valueOf(feature.getCost()));
        addressTextView.setText(String.valueOf(feature.getAddress()));

        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rating_info_window_scheduler);
        ratingBar.setRating((float)feature.getRating());
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    private class FetchFeaturesTask extends AsyncTask<Void, Void, Boolean> {

        private LatLng pos;

        public FetchFeaturesTask(LatLng pos) {
            this.pos = pos;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            FeatureList featureList = new FeatureList();
            ArrayList<Feature> features = featureList.getNearby(pos);
            if (features != null) {
                try {
                    for (Feature feature : features) {
                        Bitmap iconBmp = BitmapFactory.decodeStream(feature.getIconUrl().openConnection().getInputStream());
                        AddMarkerThread addMarkerThread = new AddMarkerThread(feature, iconBmp);
                        getActivity().runOnUiThread(addMarkerThread);
                    }
                    return true;
                } catch (Exception e) {
                    Log.d("debug", "error", e);
                }
            }
            return false;
        }

        public void onPostExecute(Boolean success) {
            if (!success) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "Error occured", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }

        private class AddMarkerThread implements Runnable {

            private Marker marker;
            private Feature feature;
            private Bitmap bmp;

            public AddMarkerThread(Feature feature, Bitmap bmp) {
                this.feature = feature;
                this.bmp = bmp;
            }

            @Override
            public void run() {
                MarkerOptions markerOptions = new MarkerOptions()
                        .title(feature.getName())
                        .position(feature.getPos())
                        .icon(BitmapDescriptorFactory.fromBitmap(bmp));
                marker = googleMap.addMarker(markerOptions);
                markerFeaturesMap.put(marker, feature);
            }
        }

    }

    private class FetchFeatureDetailsTask extends AsyncTask<Void, Void, Boolean> {

        private Marker marker;
        private Feature feature;

        public FetchFeatureDetailsTask(Marker marker) {
            this.marker = marker;
            this.feature = getFeatureByMarker(marker);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (feature.populateDetails()) {
                return true;
            }
            return false;
        }

        @Override
        public void onPostExecute(Boolean success) {
            if (success) {
                marker.showInfoWindow();
            } else {
                Log.d("debug error", "failed to populate feature");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "Error occured.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    private Feature getFeatureByMarker(Marker marker) {

        for (Marker target : markerFeaturesMap.keySet()) {
            Log.d("debug", "target " + (target == null));
            Log.d("debug", "marker " + (marker == null));
            if (marker.equals(target)) {
                return markerFeaturesMap.get(target);
            }
        }
        Log.d("debug error", "getFeaturesByMarker: not found");
        return null;
    }
}