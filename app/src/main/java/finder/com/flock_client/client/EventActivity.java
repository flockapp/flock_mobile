package finder.com.flock_client.client;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import finder.com.flock_client.R;

public class EventActivity extends AppCompatActivity {
    @BindView(R.id.pager_event) ViewPager eventViewPager;
    @BindView(R.id.tabs_event) TabLayout eventTabLayout;

    private int eventId;
    private boolean isHost;
    private EventPagerAdapter eventPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        eventId = extras.getInt("eventId", -1);
        isHost = extras.getBoolean("isHost", false);
        if (eventId == -1) {
            Log.d("debug, error", "eventId not found in intent");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        eventPagerAdapter = new EventPagerAdapter(getSupportFragmentManager());
        eventViewPager.setAdapter(eventPagerAdapter);

        eventTabLayout.setupWithViewPager(eventViewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        switch (id) {
//            case R.id.action_settings:
//                //do stufff
//        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class ActivitiesFragment extends Fragment {

        private int eventId;

        public void setEventId(int eventId) {
            this.eventId = eventId;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_event_activities, container, false);
            FloatingActionButton addActivityButton = (FloatingActionButton) view.findViewById(R.id.btn_add_activity);
            addActivityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), EventSchedulerActivity.class);
                    intent.putExtra("eventId", eventId);
                    startActivity(intent);
                }
            });
            return view;
        }
    }

    public static class IdeasFragment extends Fragment {

        private int eventId;

        public void setEventId(int eventId) {
            this.eventId = eventId;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_event_ideas, container, false);
        }
    }

    public class EventPagerAdapter extends FragmentPagerAdapter {

        public EventPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    //Sends eventId into static fragment
                    ActivitiesFragment activitiesFragment = new ActivitiesFragment();
                    activitiesFragment.setEventId(eventId);
                    return activitiesFragment;
                case 1:
                    IdeasFragment ideasFragment = new IdeasFragment();
                    ideasFragment.setEventId(eventId);
                    return ideasFragment;
            }
            Log.d("debug error", "getItem: position greater than 1");
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Activities";
                case 1:
                    return "Ideas";
            }
            return null;
        }
    }
}
