package ridemecabs.SplashScreen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ridemecabs.rideme.R;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import Entity.KeyValue;
import Entity.UserDetails;
import Infrastructure.ConnectionDetector;
import Services.Login.LoginService;
import Services.Register.IUserServiceResponse;
import Services.Register.RegisterService;
import ridemecabs.Exception.BaseAppCompatActivity;
import ridemecabs.Login.LoginActivity;
import ridemecabs.Main.MainActivity;
import ridemecabs.Register.RegisterActivity;

public class SplashScreenActivity extends BaseAppCompatActivity {

    Context context = null;
    Intent intent = null;
    Intent upIntent = null;
    Intent detailsIntent = null;
    final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        SharedPreferences settings = getSharedPreferences(getResources().getString(R.string.shared_preferences_name), 0);
        final UserDetails userDetails = gson.fromJson(settings.getString("UserDetails", null), UserDetails.class);
        String requestUrl = getString(R.string.APIBaseURL) + getString(R.string.Login);


            if (userDetails!=null) {

                new LoginService(context, new IUserServiceResponse() {
                    @Override
                    public void processFinish(String response) {
                        UserDetails userDetails =gson.fromJson(response, UserDetails.class);
                        if(userDetails!=null) {
                            if (userDetails.Id > 0) {
                                SharedPreferences settings = getSharedPreferences(getResources().getString(R.string.shared_preferences_name), 0);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("UserDetails", gson.toJson(userDetails));

                                // Commit the edits!
                                editor.commit();

                                setContentView(R.layout.activity_main);
                                Intent intent = new Intent(context, MainActivity.class);
                                intent.putExtra("UserDetails",gson.toJson(userDetails));
                                startActivity(intent);

                            } else {
                                Toast.makeText(context, getString(R.string.InvalidCredentials), Toast.LENGTH_LONG).show();
                            }
                        }
                        else
                        {
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
                }).AuthenticateUser(userDetails.Email, userDetails.Password, requestUrl);

            } else {
                setContentView(R.layout.activity_splash_screen);
                Button btnLogin = (Button) findViewById(R.id.btnLogin);
                Button btnRegister = (Button) findViewById(R.id.btnRegister);

                btnLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setContentView(R.layout.activity_login);
                        intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                    }
                });


                detailsIntent = new Intent(this, RegisterActivity.class);
                upIntent = new Intent(this, SplashScreenActivity.class);
                btnRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setContentView(R.layout.activity_register);
                        intent = new Intent(context, RegisterActivity.class);

                        startActivity(intent);

                    }
                });
            }
    }
}
