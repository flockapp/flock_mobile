package finder.com.flock_client.client;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import finder.com.flock_client.R;
import finder.com.flock_client.client.api.User;

/**
 * Created by Daniel on 4/6/16.
 */

public class RegisterActivity extends AppCompatActivity{
    @BindView(R.id.input_name) public EditText _fullName;
    @BindView(R.id.input_username) public EditText _username;
    @BindView(R.id.input_password) public EditText _password;
    @BindView(R.id.input_confirm_password) public EditText _confirmPassword;

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_register);


        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating User...");

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_signup)
    public void signUpButtonClicked() {
        if (validateSignup()) {
            progressDialog.show();

            String fullName = _fullName.getText().toString();
            String username = _username.getText().toString();
            String password = _password.getText().toString();
            new RegisterTask(fullName, username, password).execute();
        }
    }

    @OnClick(R.id.link_login)
    public void loginLinkClicked() {
        //Return to login activity
        finish();
    }

    private void onLoginFailed(String msg) {
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
    }

    private boolean validateSignup() {
        boolean valid = true;

        String fullName = _fullName.getText().toString();
        String username = _username.getText().toString();
        String password = _password.getText().toString();
        String confirmPassword = _confirmPassword.getText().toString();
        if (!password.equals(confirmPassword)) {
            _confirmPassword.setError("Passwords do not match");
            valid = false;
        } else if (password.length() < 8) {
            _password.setError("Password must be at least 8 characters");
        }
        if (fullName.length() == 0) {
            _fullName.setError("Name cannot be empty");
            valid = false;
        }
        if (username.length() == 0) {
            _username.setError("Username cannot be empty");
            valid = false;
        }
        return valid;
    }

    private class RegisterTask extends AsyncTask<Void, Void, String> {

        private String username;
        private String fullName;
        private String password;

        public RegisterTask(String username, String fullName, String password) {
            this.username = username;
            this.fullName = fullName;
            this.password = password;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                User user = new User();
                JSONObject resp = user.register(fullName, username, password);
                if (resp.getBoolean("success")) {
                    return null; //Successfully created user;
                } else {
                    return resp.getString("message");
                }

            } catch (Exception e) {
                Log.d("debug error", e.toString());
                return "Client Error";
            }
        }

        @Override
        protected void onPostExecute(String resp) {
            progressDialog.hide();
            if (resp == null) {
                //End Activity
                setResult(RESULT_OK);
                finish();
            } else {
                onLoginFailed(resp);
            }
        }
    }
}
