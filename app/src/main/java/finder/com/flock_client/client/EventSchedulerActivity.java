package finder.com.flock_client.client;

import android.Manifest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import finder.com.flock_client.FlockApplication;
import finder.com.flock_client.R;
import finder.com.flock_client.client.api.Event;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by Daniel on 15/6/16.
 */

@RuntimePermissions
public class EventSchedulerActivity extends AppCompatActivity {
    private GoogleMap googleMap;

    private SchedulerMapFragment mainFragment;
    private SchedulerRecommendationsFragment recommendationsFragment;
    private int eventId;

    protected Event event;

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_scheduler_event);

        //Initializing fragments
        mainFragment = new SchedulerMapFragment();
        recommendationsFragment = new SchedulerRecommendationsFragment();

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

    public static class SchedulerMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnCameraChangeListener {

        private Event event;
        private GoogleMap googleMap;

        public static Fragment newInstance() {
            return new SchedulerMapFragment();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View mainView = inflater.inflate(R.layout.fragment_scheduler_main, container, false);

            SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map_event_scheduler);
            mapFragment.getMapAsync(this);

            return mainView;
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            this.googleMap = googleMap;
            googleMap.setOnCameraChangeListener(this);
        }

        public void initMap() {

        }

        public void setEvent(Event event) {
            this.event = event;
            if (googleMap != null) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(event.getLat(), event.getLng()), 12
                ));
            }
        }

        //Retrieves features around camera radius using google places web services
        public void getFeaturesAroundCamera(LatLng pos) {

        }

        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
            getFeaturesAroundCamera(cameraPosition.target);
        }
    }

    public static class SchedulerRecommendationsFragment extends Fragment {

        private Event event;

        public static Fragment newInstance() {
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
