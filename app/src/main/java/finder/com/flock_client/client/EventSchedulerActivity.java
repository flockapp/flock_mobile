package finder.com.flock_client.client;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import finder.com.flock_client.FlockApplication;
import finder.com.flock_client.R;
import finder.com.flock_client.client.api.Event;
import finder.com.flock_client.client.api.Feature;
import finder.com.flock_client.client.api.FeatureList;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by Daniel on 15/6/16.
 */

@RuntimePermissions
public class EventSchedulerActivity extends AppCompatActivity {

    private SchedulerMainFragment mainFragment;
    private SchedulerRecommendationsFragment recommendationsFragment;
    private int eventId;

    protected Event event;

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_scheduler_event);

        //Initializing fragments
        mainFragment = SchedulerMainFragment.newInstance();
        recommendationsFragment = SchedulerRecommendationsFragment.newInstance();

        //Tabs setup
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager_scheduler);
        FragmentPagerAdapter pagerAdapter = new SchedulerPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        Bundle extras = getIntent().getExtras();
        eventId = extras.getInt("eventId");

        new FetchEventDetailsTask(eventId).execute();
    }

    private class FetchEventDetailsTask extends AsyncTask<Void, Void, Event> {

        private int eventId;

        public FetchEventDetailsTask(int eventId) {
            this.eventId = eventId;
        }


        @Override
        protected Event doInBackground(Void... params) {
            Event event = new Event(eventId, ((FlockApplication) getApplication()).getCurrToken());
            boolean success = event.populateDetails();
            if (success) {
                return event;
            }
            return null;
        }

        @Override
        public void onPostExecute(Event event) {
            if (event != null) {
                mainFragment.setEvent(event);
                recommendationsFragment.setEvent(event);
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Error occured.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    public static class SchedulerMainFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnCameraChangeListener,
        GoogleMap.OnMarkerClickListener, GoogleMap.InfoWindowAdapter {

        private Event event;
        private GoogleMap googleMap;
        private SupportMapFragment mapFragment;
        private boolean tapped = false;
        private LatLng previousTarget = new LatLng(0, 0); //Default prev value

        public static SchedulerMainFragment newInstance() {
            return new SchedulerMainFragment();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_scheduler_main, container, false);
        }

        @Override
        public void onActivityCreated(Bundle b) {
            super.onActivityCreated(b);
            FragmentManager fm = getChildFragmentManager();
            mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_event_scheduler);
            if (mapFragment == null) {
                mapFragment = SupportMapFragment.newInstance();
                fm.beginTransaction().replace(R.id.map_event_scheduler, mapFragment).commit();
            }
        }

        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
            Log.d("debug cameraZoom", cameraPosition.zoom+"");
            LatLng currentTarget = cameraPosition.target;
            //If marker was not tapped, zoom is beyond threshold and camera is moved (not zoomed)
            if (!tapped && cameraPosition.zoom > 13 && !previousTarget.equals(currentTarget)) {
                googleMap.clear(); //Removes all markers
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
            //SHOW INFO WINDOW
            return true; //Disables default functionality
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {return null;}

        private class FetchFeaturesTask extends AsyncTask<Void, Void, Boolean> {

            private LatLng pos;

            public FetchFeaturesTask(LatLng pos) {
                this.pos = pos;
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                FeatureList featureList = new FeatureList();
                ArrayList<Feature> features = featureList.getNearby(pos);
                if (features != null ) {
                    try {
                        for (final Feature feature : features) {
                            final Bitmap iconBmp = BitmapFactory.decodeStream(feature.getIconUrl().openConnection().getInputStream());
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MarkerOptions markerOptions = new MarkerOptions()
                                            .title(feature.getName())
                                            .position(feature.getPos())
                                            .icon(BitmapDescriptorFactory.fromBitmap(iconBmp));
                                    googleMap.addMarker(markerOptions);
                                }
                            });
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
        }
    }

    public static class SchedulerRecommendationsFragment extends Fragment {

        private Event event;

        public static SchedulerRecommendationsFragment newInstance() {
            return new SchedulerRecommendationsFragment();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_scheduler_recommendations, container, false);
        }

        public void setEvent(Event event) {
            this.event = event;
        }
    }

    public class SchedulerPagerAdapter extends FragmentPagerAdapter {

        public SchedulerPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    return mainFragment;
                case 1:
                    return recommendationsFragment;
            }
            return null;
        }

    }
}
