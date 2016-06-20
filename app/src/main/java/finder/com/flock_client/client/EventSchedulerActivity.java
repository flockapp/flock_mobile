package finder.com.flock_client.client;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import finder.com.flock_client.FlockApplication;
import finder.com.flock_client.R;
import finder.com.flock_client.client.api.Event;
import finder.com.flock_client.client.api.Feature;
import finder.com.flock_client.client.api.FeatureList;
import finder.com.flock_client.client.fragments.SchedulerMainFragment;
import finder.com.flock_client.client.fragments.SchedulerRecommendationsFragment;
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
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_scheduler);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_scheduler);

        FragmentPagerAdapter pagerAdapter = new SchedulerPagerAdapter(getSupportFragmentManager());

        setSupportActionBar(toolbar);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        Bundle extras = getIntent().getExtras();
        eventId = extras.getInt("eventId");

        new FetchEventDetailsTask(eventId).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scheduler, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        return true;
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
            switch (position) {
                case 0:
                    return mainFragment;
                case 1:
                    return recommendationsFragment;
            }
            return null;
        }
    }
}
