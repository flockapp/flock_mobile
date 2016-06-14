package finder.com.flock_client.client;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
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
    private static ArrayList<Event> clonedEventList = new ArrayList<>();
    private EventAdapter eventAdapter;
    private SearchView searchView;


    @BindView(R.id.list_dash)
    public ListView dashList;

    @BindView(R.id.toolbar_dash)
    public Toolbar toolbar;

    @BindView(R.id.empty_dash)
    public TextView emptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);
        ButterKnife.bind(this);

        new FetchEventListTask().execute();
        dashList.setEmptyView(emptyText);
        toolbar.setTitle("Events");
        setSupportActionBar(toolbar);
    }

    @OnClick(R.id.btn_to_event_create)
    public void onCreateEventButtonClicked() {
        Intent intent = new Intent(this, CreateEventActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dash, menu);
        MenuItem searchItem = menu.findItem(R.id.item_dash_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(
                new ComponentName(this, DashActivity.class))); //
        searchView.setIconifiedByDefault(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_dash_search:
        }
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case Intent.ACTION_SEARCH:
                    String query = intent.getStringExtra(SearchManager.QUERY);
                    Log.d("debug query", query);
                    eventAdapter.clear();
                    for (Event evt : clonedEventList) {
                        if (evt.getName().contains(query)) {
                            eventAdapter.add(evt);
                        }
                    }
                    eventAdapter.notifyDataSetChanged();
                    break;
                case Intent.ACTION_VIEW:
                    int eventId = Integer.parseInt(intent.getData().getLastPathSegment());
                    Intent newIntent = new Intent(DashActivity.this, EventActivity.class);
                    Log.d("debug intent", "eventId " + eventId);
                    newIntent.putExtra("eventId", eventId);
                    startActivity(newIntent);
                    break;
            }
        }
    }

    //Back Button Listener
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d("debug back", "pressed");
            eventAdapter.clear();
            eventAdapter.addAll(new ArrayList<>(clonedEventList));
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
        public void onPostExecute(final ArrayList<Event> evtList) {
            clonedEventList = new ArrayList<>(evtList);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (evtList != null) {
                        //Set listview adapter
                        eventAdapter = new EventAdapter(getApplicationContext(), R.layout.item_dash, evtList);
                        dashList.setAdapter(eventAdapter);
                        eventAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getBaseContext(), "Failed to retrieve events", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public static class EventSuggestionProvider extends ContentProvider {

        public static final String AUTHORITY = "finder.com.flock_client.eventProvider";

        public EventSuggestionProvider() {
            super();
        }

        @Override
        public boolean onCreate() {
            return false;
        }

        @Nullable
        @Override
        public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
            if (clonedEventList != null) {
                MatrixCursor cursor = new MatrixCursor(new String[]{ //Table
                        BaseColumns._ID,
                        SearchManager.SUGGEST_COLUMN_TEXT_1,
                        SearchManager.SUGGEST_COLUMN_INTENT_DATA,
                        SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID
                });
                String query = uri.getLastPathSegment(); //Query
                Log.d("debug query", query);
                int limit = Integer.parseInt(uri.getQueryParameter(SearchManager.SUGGEST_PARAMETER_LIMIT));

                int length = clonedEventList.size();
                for (int i = 0; i < length && cursor.getCount() < limit; i++) {
                    Event event = clonedEventList.get(i);
                    String eventName = event.getName();
                    if (eventName.toUpperCase().contains(query.toUpperCase())) {
                        cursor.addRow(new Object[]{i, eventName, event.getId(), event.getId()});
                    }
                }
                return cursor;
            }
            return null;
        }

        @Nullable
        @Override
        public String getType(Uri uri) {
            return null;
        }

        @Nullable
        @Override
        public Uri insert(Uri uri, ContentValues values) {
            return null;
        }

        @Override
        public int delete(Uri uri, String selection, String[] selectionArgs) {
            return 0;
        }

        @Override
        public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
            return 0;
        }
    }
}