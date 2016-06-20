package finder.com.flock_client.client.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import finder.com.flock_client.R;
import finder.com.flock_client.client.api.Event;

/**
 * Created by Daniel on 20/6/2016.
 */

public class SchedulerRecommendationsFragment extends Fragment {

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
