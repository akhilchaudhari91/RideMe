package ridemecabs.Register;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ridemecabs.rideme.R;
import com.google.gson.Gson;

import org.json.JSONObject;

import Entity.RideNowModel;
import Entity.User;
import Entity.UserDetails;
import Infrastructure.ConnectionDetector;
import Services.Enum.EnumSharedPreferences;
import Services.Register.IUserServiceResponse;
import Services.Register.OTPService;
import api.AsyncResponse;
import ridemecabs.Main.MainActivity;

public class OTPActivity extends AppCompatActivity {

    Button btnVerify;
    TextView txtVIewOTP;
    private Context context;
    String requestUrl;
    final Gson gson = new Gson();
    boolean cancel = false;
    View focusView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        context = this;

        txtVIewOTP = ((TextView)findViewById(R.id.txtOTP));
        btnVerify = ((Button)findViewById(R.id.btnVerify));

        txtVIewOTP.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.txtOTP || id == EditorInfo.IME_NULL) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    CheckValidations();
                    return true;
                }
                return false;
            }
        });
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckValidations();
            }
        });
    }

    private boolean CheckValidations()
    {
        cancel=false;
        txtVIewOTP.setError(null);

        // Store values at the time of the register attempt.
        String otp = txtVIewOTP.getText().toString();

        if(TextUtils.isEmpty(otp))
        {
            cancel=true;
            txtVIewOTP.setError(getString(R.string.invalid_otp));
            focusView = txtVIewOTP;
        }
        else if (!TextUtils.isDigitsOnly(otp))
        {
         cancel=true;
            txtVIewOTP.setError(getString(R.string.invalid_otp));
            focusView = txtVIewOTP;
        }
        else if (otp.trim().length()!=6)
        {
            cancel=true;
            txtVIewOTP.setError(getString(R.string.invalid_otp));
            focusView = txtVIewOTP;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            requestUrl = getString(R.string.APIBaseURL) + getString(R.string.VerifyOTP);
            User userDetails = gson.fromJson(this.getIntent().getSerializableExtra(EnumSharedPreferences.UserDetails.toString()).toString(), User.class);
            new OTPService(context, new AsyncResponse() {
                @Override
                public void processFinish(String response) {
                    try {
                        if(!TextUtils.isEmpty(response)) {
                            JSONObject obj = new JSONObject(response);
                            if(obj.getString("Status").equals("Error"))
                            {
                                Toast.makeText(context, getString(R.string.error_otp_validation), Toast.LENGTH_LONG).show();
                            }
                            else if(obj.getString("Status").equals("Verified"))
                            {
                                SharedPreferences settings = getSharedPreferences(EnumSharedPreferences.UserDetails.toString(), 0);
                                SharedPreferences.Editor editor = settings.edit();
                                final UserDetails userDetails = gson.fromJson(settings.getString(EnumSharedPreferences.UserDetails.toString(), null), UserDetails.class);
                                userDetails.ContactVerificationStatus=2;
                                editor.putString(EnumSharedPreferences.UserDetails.toString(), gson.toJson(userDetails));
                                editor.apply();

                                Intent intent = new Intent(context, MainActivity.class);
                                RideNowModel model = new RideNowModel();
                                model.RideStatus = 1;
                                intent.putExtra(EnumSharedPreferences.RideNowModel.toString(),gson.toJson(model));
                                startActivity(intent);
                            }
                        }
                        else
                        {
                            ConnectionDetector connectionObj = new ConnectionDetector(context);
                            try {
                                if (connectionObj.execute().get()) {
                                    cancel=true;
                                    txtVIewOTP.setError(getString(R.string.invalid_otp));
                                    focusView = txtVIewOTP;
                                } else {
                                    Toast.makeText(context, getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception ex) {
                                Log.d("Connection Detector: ", ex.getMessage());
                            }
                        }

                    } catch (Exception ex) {
                        Log.d("OTPVerification: ", ex.getMessage());
                    }
                }
            }).VerifyOTP(userDetails.AccessToken,userDetails.Id,Integer.parseInt(otp), requestUrl);
        }
        return cancel;

    }
}
