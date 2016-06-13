package finder.com.flock_client.client;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import finder.com.flock_client.FlockApplication;
import finder.com.flock_client.R;
import finder.com.flock_client.client.api.Event;
import finder.com.flock_client.client.api.EventList;

public class DashActivity extends AppCompatActivity {
    @BindView(R.id.list_dash)
    public ListView dashList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);

        new FetchEventListTask().execute();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_dash);
        toolbar.setTitle("Events");
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_to_event_create)
    public void onCreateEventButtonClicked() {
        Intent intent = new Intent(this, CreateEventActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dash, menu);
        return true;
    }

    private class EventAdapter extends ArrayAdapter<Event> {

        private int layout;

        private EventAdapter(Context context, int resource, List<Event> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder mainViewHolder;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.rightButton = (ImageButton) convertView.findViewById(R.id.btn_event_right);
                viewHolder.name = (TextView) convertView.findViewById(R.id.text_event_name);
                viewHolder.date = (TextView) convertView.findViewById(R.id.text_event_date);
                viewHolder.time = (TextView) convertView.findViewById(R.id.text_event_time);
                convertView.setTag(viewHolder);
            }
            mainViewHolder = (ViewHolder) convertView.getTag();
            //Render viewholder content
            final Event event = getItem(position);
            mainViewHolder.name.setText(event.getName());
            mainViewHolder.date.setText(event.getDate());
            mainViewHolder.time.setText(event.getTime());
            mainViewHolder.rightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Start EventActivity
                    Log.d("debug", "right button clicked");
                    Intent intent = new Intent(DashActivity.this, EventActivity.class);
                    intent.putExtra("eventId", event.getId());
                    intent.putExtra("eventName", event.getName());
                    intent.putExtra("isHost", true);
                    startActivity(intent);
                }
            });
            return convertView;
        }

        public class ViewHolder {
            ImageButton rightButton;
            TextView name;
            TextView date;
            TextView time;
        }
    }

    private class FetchEventListTask extends AsyncTask<Void, Void, ArrayList<Event>> {

        @Override
        protected ArrayList<Event> doInBackground(Void... params) {
            EventList evtList = new EventList(((FlockApplication) getApplication()).getCurrToken());
            try {
                evtList.retrieveEvents(); //Make get request to server
                return evtList.getEventList();
            } catch (Exception e) {
                Log.d("debug", "error", e);
                return null;
            }
        }

        @Override
        public void onPostExecute(final ArrayList<Event> eventList) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (eventList != null) {
                        //Set listview adapter
                        EventAdapter la = new EventAdapter(getApplicationContext(), R.layout.item_dash, eventList);
                        dashList.setAdapter(la);
                        la.notifyDataSetChanged();
                        dashList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        });
                    } else {
                        Toast.makeText(getBaseContext(), "Failed to retrieve events", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }


}
