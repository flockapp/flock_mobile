package finder.com.flock_client.client;

import android.Manifest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
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
public class EventSchedulerActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private int eventId;

    protected Event event;

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_scheduler_event);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_event_scheduler);
        mapFragment.getMapAsync(this);

        Bundle extras = getIntent().getExtras();
        eventId = extras.getInt("eventId");

        new FetchEventDetailsTask(eventId).execute();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        Log.d("debug map", "map initialized");
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public void initMap() {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(event.getLat(), event.getLng()), 13
        ));
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
                setEvent(event);
                initMap();
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
}
