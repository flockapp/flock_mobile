package finder.com.flock_client.client;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import finder.com.flock_client.FlockApplication;
import finder.com.flock_client.R;
import finder.com.flock_client.client.api.Event;
import finder.com.flock_client.client.api.Type;
import finder.com.flock_client.client.api.TypeList;

/**
 * Created by Daniel on 7/6/16.
 */

public class CreateEventActivity extends AppCompatActivity implements OnMapReadyCallback {
    @BindView(R.id.input_event_name)
    public EditText eventNameText;
    @BindView(R.id.seekbar_event_cost)
    public SeekBar seekBar;
    @BindView(R.id.label_event_date)
    public TextView eventDateLabel;
    @BindView(R.id.label_event_time)
    public TextView eventTimeLabel;
    @BindView(R.id.list_event_type)
    public ListView eventTypeList;

    private LatLng eventLoc;
    private Calendar eventCalendar;
    private ArrayList<Integer> typeIds = new ArrayList<>();

    private int costVal = 2;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_create_event);
        ButterKnife.bind(this);

        //Fetches and populates type list
        new FetchTypesTask().execute();

        //Init map fragment
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_create_event);
        mapFragment.getMapAsync(this);

        eventCalendar = Calendar.getInstance();

        seekBar.setProgress(costVal); //Default Cost Value of seekbar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                costVal = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @OnClick(R.id.btn_event_date)
    public void onEventDateButtonClicked() {
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.show(getSupportFragmentManager(), "datePicker");
    }

    @OnClick(R.id.btn_event_time)
    public void onEventTimeButtonClicked() {
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.show(getSupportFragmentManager(), "timePicker");
    }

    @OnClick(R.id.btn_create_event)
    public void onCreateEventButtonClicked() {
        Log.d("debug", typeIds.toString());
        if (validateEventDetails()) {
            new CreateEventTask(((FlockApplication) getApplication()).getCurrToken(),
                    eventNameText.getText().toString(), //Name
                    eventCalendar.getTime().getTime(), //Time in unix time
                    costVal,
                    eventLoc.latitude,
                    eventLoc.longitude
            ).execute();
        }
    }

    private boolean validateEventDetails() {
        Boolean valid = true;
        if (eventNameText.getText().toString().equals("")) {
            valid = false;
            eventNameText.setError("Event name cannot be empty");
        }
        if (eventLoc == null) {
            Toast.makeText(getApplicationContext(), "Please select event location", Toast.LENGTH_LONG).show();
            valid = false;
        }
        return valid;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(22.396428, 114.109497), 12));
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                eventLoc = latLng;
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(eventLoc));
            }
        });
    }

    private class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            int hour = eventCalendar.get(Calendar.HOUR_OF_DAY);
            int minute = eventCalendar.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            eventCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            eventCalendar.set(Calendar.MINUTE, minute);
            eventTimeLabel.setText(padDigits(hourOfDay) + ":" + padDigits(minute));
        }
    }

    private class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int year = eventCalendar.get(Calendar.YEAR);
            int month = eventCalendar.get(Calendar.MONTH);
            int day = eventCalendar.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            eventCalendar.set(Calendar.MONTH, month);
            eventCalendar.set(Calendar.YEAR, year);
            eventCalendar.set(Calendar.DAY_OF_MONTH, day);
            eventDateLabel.setText(new DateFormatSymbols().getMonths()[month] + " " + padDigits(day) + ", " + year);
        }
    }

    private class FetchTypesTask extends AsyncTask<Void, Void, ArrayList<Type>> {

        @Override
        protected ArrayList<Type> doInBackground(Void... params) {

            TypeList typeList = new TypeList(((FlockApplication) getApplication()).getCurrToken());
            try {
                typeList.getTypes();
                return typeList.getTypeList();
            } catch (Exception e) {
                Log.d("debug", "error", e);
                return null;
            }
        }

        public void onPostExecute(ArrayList<Type> resp) {
            if (resp != null) {
                ArrayList<Pair<Type, Type>> typeArray = new ArrayList<>();
                try {
                    for (int i = 0; i < resp.size(); i += 2) {
                        if (i + 1 < resp.size()) {
                            typeArray.add(new Pair<>(resp.get(i), resp.get(i+1)));
                        } else {
                            typeArray.add(new Pair<>(resp.get(i), new Type(-1, "")));
                        }
                    }
                    TypeListAdapter la = new TypeListAdapter(getBaseContext(), R.layout.item_event_type, typeArray);
                    eventTypeList.setAdapter(la);
                    la.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.d("debug", "error", e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Client Error", Toast.LENGTH_LONG).show();
                        }
                    });

                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Client Error", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    private class CreateEventTask extends AsyncTask<Void, Void, JSONObject> {

        private String token;
        private String name;
        private long time;
        private int cost;
        private double lat;
        private double lng;

        public CreateEventTask(String token, String name, long time, int cost, double lat, double lng) {
            this.token = token;
            this.name = name;
            this.time = time;
            this.cost = cost;
            this.lat = lat;
            this.lng = lng;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            Event event = new Event(token, name, time, cost, typeIds.toArray(new Integer[typeIds.size()]), lat, lng);
            try {
                JSONObject resp = event.createEvent();
                return resp;
            } catch (Exception e) {
                Log.d("debug", "error", e);
                return null;
            }
        }

        public void onPostExecute(final JSONObject resp) {
            try {
                if (resp.getBoolean("success")) {
                    Intent intent = new Intent(CreateEventActivity.this, GuestInviteActivity.class);
                    intent.putExtra("first", true); //Indicator that this is the first time guest invite page is entered
                    intent.putExtra("eventId", resp.getJSONObject("data").getInt("id"));
                    startActivity(intent);
                    finish();
                } else {
                    final String errorMessage = resp.getString("message");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (JSONException e) {
                Log.d("debug", "error", e);
            }
        }
    }

    private class TypeListAdapter extends ArrayAdapter<Pair<Type, Type>> {
        private int layout;

        public TypeListAdapter(Context context, int resource, List<Pair<Type, Type>> objects) {
            super(context, resource, objects);
            layout = resource;

        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder mainViewHolder = null;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getBaseContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.firstType = (TextView) convertView.findViewById(R.id.type_event_item_1);
                viewHolder.secondType = (TextView) convertView.findViewById(R.id.type_event_item_2);
                convertView.setTag(viewHolder);
            }
            mainViewHolder = (ViewHolder) convertView.getTag();

            //Ensuring that both views are visible
            mainViewHolder.firstType.setVisibility(View.VISIBLE);
            mainViewHolder.secondType.setVisibility(View.VISIBLE);

            Pair<Type, Type> row = getItem(position);
            mainViewHolder.firstType.setText(row.first.getName());
            mainViewHolder.firstType.setTag(row.first.getId());
            //Type textview click listener
            mainViewHolder.firstType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("debug", typeIds.toString());
                    if (!typeIds.contains(v.getTag())) {
                        //If type not in array
                        typeIds.add((int) v.getTag());
                        v.setBackgroundColor(getResources().getColor(R.color.colorAccent, getTheme()));
                    } else {
                        typeIds.remove(v.getTag());
                        v.setBackgroundColor(getResources().getColor(R.color.colorGray, getTheme()));
                    }
                }
            });
            if (!(row.second.getId() == -1)) {
                mainViewHolder.secondType.setText(row.second.getName());
                mainViewHolder.secondType.setTag(row.second.getId());
                mainViewHolder.secondType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("debug", typeIds.toString());
                        if (!typeIds.contains(v.getTag())) {
                            //If type not in array
                            typeIds.add((int) v.getTag());
                            v.setBackgroundColor(getResources().getColor(R.color.colorAccent, getTheme()));
                        } else {
                            typeIds.remove(v.getTag());
                            v.setBackgroundColor(getResources().getColor(R.color.colorGray, getTheme()));
                        }
                    }
                });
            } else {
                mainViewHolder.secondType.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }

        private class ViewHolder {
            TextView firstType;
            TextView secondType;
        }

    }

    private String padDigits(int time) {
        if (time < 10) {
            return "0" + time;
        }
        return "" + time;
    }
}
