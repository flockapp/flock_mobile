package finder.com.flock_client.client;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import finder.com.flock_client.FlockApplication;
import finder.com.flock_client.R;
import finder.com.flock_client.client.api.User;

public class LoginActivity extends AppCompatActivity {

    private final int REQUEST_SIGNUP = 0;
    @BindView(R.id.input_username)
    public EditText _username;
    @BindView(R.id.input_password)
    public EditText _password;
    @BindView(R.id.btn_login)
    public Button _loginButton;

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Give ProgressDialog non-app theme so that it won't take up full screen
        progressDialog = new ProgressDialog(LoginActivity.this, android.R.style.Theme_Material_Light_Dialog);
        setContentView(R.layout.activity_login);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_login)
    public void loginButtonClicked() {
        if (validateLogin()) {
            _loginButton.setEnabled(false);
            progressDialog.show();
            String username = _username.getText().toString();
            String password = _password.getText().toString();
            new LoginTask(username, password).execute();
        }
    }

    @OnClick(R.id.link_signup)
    public void signupLinkClicked() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivityForResult(intent, REQUEST_SIGNUP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getBaseContext(), "Successfully created account", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean validateLogin() {
        boolean valid = true;

        String username = _username.getText().toString();
        String password = _password.getText().toString();

        if (password.length() < 8) {
            _password.setError("Password needs to be at least 8 characters");
            valid = false;
        }
        if (username.length() == 0) {
            _username.setError("Username cannot be empty");
            valid = false;
        }

        return valid;
    }

    private void onLoginSuccess() {
        _loginButton.setEnabled(true);
        //Move onto event dashboard
        Intent intent = new Intent(this, DashActivity.class);
        startActivity(intent);
        finish();
    }

    private void onLoginFailed(String msg) {
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private class LoginTask extends AsyncTask<Void, Void, Pair<String, Boolean>> {
        private String username;
        private String password;

        public LoginTask(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected Pair<String, Boolean> doInBackground(Void... params) {
            try {
                boolean success = false;
                User user = new User(username, password);
                JSONObject resp = user.login();
                if (user.loginSuccess()) {
                   success = true;
                    ((FlockApplication)getApplication()).setCurrToken(resp.getString("data"));
                }
                return new Pair<>(resp.getString("message"), success);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("debug error", "system", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Pair<String, Boolean> resp) {
            progressDialog.hide();
            if (resp != null) {
                if (resp.second) {
                    onLoginSuccess();
                } else {
                    onLoginFailed(resp.first);
                }
            } else {
                onLoginFailed("Client Error");
            }
        }
    }
}