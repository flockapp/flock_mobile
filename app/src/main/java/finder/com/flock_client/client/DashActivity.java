package finder.com.flock_client.client;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import butterknife.BindView;
import finder.com.flock_client.R;

public class DashActivity extends AppCompatActivity {
    @BindView(R.id.btn_create_event) public FloatingActionButton createEventButton;
    @BindView(R.id.item_dash_join) public MenuView.ItemView joinEventItem;
    @BindView(R.id.item_dash_search) public MenuView.ItemView searchEventItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_dash);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dash, menu);
        return true;
    }


}
