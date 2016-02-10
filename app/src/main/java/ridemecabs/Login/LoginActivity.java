package ridemecabs.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ridemecabs.rideme.R;
import com.google.gson.Gson;

import Entity.User;
import Entity.UserDetails;
import Infrastructure.ConnectionDetector;
import Services.Login.LoginService;
import Services.Register.IUserServiceResponse;
import ridemecabs.Main.MainActivity;
import ridemecabs.SplashScreen.SplashScreenActivity;

public class LoginActivity extends AppCompatActivity {

    User user = null;
    Context context;
    boolean cancel = false;
    final Gson gson = new Gson();

    private EditText mEmailView;
    private EditText mPasswordView;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context=this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailView = (EditText) findViewById(R.id.login_email);
        mPasswordView = (EditText) findViewById(R.id.login_password);
        btnLogin = (Button) findViewById(R.id.login_button);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!CheckValidations())
                {
                    attemptLogin();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, SplashScreenActivity.class);
        startActivity(intent);
    }

    private void attemptLogin() {
        try {
            String email = mEmailView.getText().toString();
            final String password = mPasswordView.getText().toString();

            String requestUrl = getString(R.string.APIBaseURL) + getString(R.string.Login);
            byte[] data = password.getBytes("UTF-8");
            String base64Password = Base64.encodeToString(data, Base64.DEFAULT);

            new LoginService(context, new IUserServiceResponse() {
                @Override
                public void processFinish(String response) {
                    UserDetails userDetails = gson.fromJson(response, UserDetails.class);
                    if (userDetails != null) {
                        if (userDetails.Id > 0) {
                            SharedPreferences settings = getSharedPreferences(getResources().getString(R.string.shared_preferences_name), 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("UserDetails", gson.toJson(userDetails));

                            // Commit the edits!
                            editor.commit();

                            setContentView(R.layout.activity_main);
                            Intent intent = new Intent(context, MainActivity.class);
                            startActivity(intent);

                        } else {
                            Toast.makeText(context, getString(R.string.InvalidCredentials), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        ConnectionDetector connectionObj = new ConnectionDetector(context);
                        try {
                            if (connectionObj.execute().get()) {
                                Toast.makeText(context, getString(R.string.error_Login), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception ex) {
                            Log.d("Connection Detector", ex.getMessage());
                        }
                    }
                }
            }).AuthenticateUser(email, base64Password, requestUrl);
        } catch (Exception ex) {
            Log.d("Login: ", ex.getMessage());
        }
    }

    private boolean CheckValidations() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        cancel = false;

        // Store values at the time of the register attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email) || !isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        }
        return cancel;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }
}
