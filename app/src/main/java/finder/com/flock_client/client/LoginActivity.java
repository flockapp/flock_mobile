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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import finder.com.flock_client.R;
import finder.com.flock_client.client.api.User;

public class LoginActivity extends AppCompatActivity {

    private final int REQUEST_SIGNUP = 0;
    @BindView(R.id.input_username)
    private EditText _username;
    @BindView(R.id.input_password)
    private EditText _password;
    @BindView(R.id.btn_login)
    private Button _loginButton;

    private ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_login)
    private void loginButtonClicked() {
        if (validateLogin()) {
            _loginButton.setEnabled(false);
            progressDialog.show();
            String username = _username.getText().toString();
            String password = _password.getText().toString();
            new LoginTask(username, password).execute();
        }
    }

    @OnClick(R.id.link_signup)
    private void signupLinkClicked() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivityForResult(intent, REQUEST_SIGNUP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                onLoginSuccess();
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
        //Successful Signup Logic
        finish();
    }

    private void onLoginFailed(String msg) {
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
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
                String msg = user.login();
                if (user.loginSuccess()) {
                   success = true;
                }
                return new Pair<>(msg, success);
            } catch (Exception e) {
                Log.d("debug error", e.toString());
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