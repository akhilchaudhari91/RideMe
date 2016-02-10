package ridemecabs.Register;

import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ridemecabs.rideme.R;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import Entity.KeyValue;
import Entity.User;
import Entity.UserDetails;
import Infrastructure.ConnectionDetector;
import Services.Register.IUserServiceResponse;
import Services.Register.RegisterService;
import ridemecabs.Main.MainActivity;
import ridemecabs.SplashScreen.SplashScreenActivity;

public class RegisterActivity extends AppCompatActivity {

    User user = null;
    Context context;
    boolean cancel = false;
    boolean isDuplicateContact = false;
    boolean isDuplicateEmail = false;


    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private EditText mCustomerNameView;
    private EditText mContactView;

    final Gson gson = new Gson();
    RegisterService registerService;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        try {

            context = RegisterActivity.this;
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register);

            mEmailView = (EditText) findViewById(R.id.email);
            mPasswordView = (EditText) findViewById(R.id.password);
            mConfirmPasswordView = (EditText) findViewById(R.id.confirmPassword);
            mCustomerNameView = (EditText) findViewById(R.id.customerName);
            mContactView = (EditText) findViewById(R.id.mobileNumber);

            /* String email = ReadOwnerData.getEmail(this);

            TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
            String number = tm.getLine1Number();

            if(email!=null && TextUtils.isEmpty(email))
            {
                mEmailView.setText(email);
            } */

            final Button mEmailRegisterButton = (Button) findViewById(R.id.email_register_button);

            mConfirmPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.id.registerConfirmPassword || id == EditorInfo.IME_NULL) {
                        CheckValidations();
                        return true;
                    }
                    return false;
                }
            });
            mEmailRegisterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckValidations();
                }
            });
        } catch (Exception ex) {
            Log.d("Validations: ", ex.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, SplashScreenActivity.class);
        startActivity(intent);
    }

    private void attemptRegister() {

        String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();
        String contactNo = mContactView.getText().toString();
        String customerName = mCustomerNameView.getText().toString();

            user = new User();
            user.Name = customerName;
            user.Email = email;
            user.ContactNo = contactNo;
            user.LastLocation = "";
            user.IsActive = true;

            try {
                byte[] data = password.getBytes("UTF-8");
                user.Password = Base64.encodeToString(data, Base64.DEFAULT);

                String requestUrl = getString(R.string.APIBaseURL) + getString(R.string.RegisterUser);

               new RegisterService(context, new IUserServiceResponse() {
                   @Override
                   public void processFinish(String response) {
                           UserDetails registerDetails = gson.fromJson(response,UserDetails.class);
                           AsyncTaskResult(registerDetails);
                   }
               }).RegisterUser(user, requestUrl);

            } catch (Exception ex) {
                Log.d("RegisterActivity", ex.getMessage());
            }
    }

    private boolean CheckValidations() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mConfirmPasswordView.setError(null);
        mContactView.setError(null);
        mCustomerNameView.setError(null);
        cancel = false;

        // Store values at the time of the register attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confirmPassword = mConfirmPasswordView.getText().toString();
        String contactNo = mContactView.getText().toString();
        String customerName = mCustomerNameView.getText().toString();

        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        } else if (!TextUtils.isEmpty(confirmPassword) && !isPasswordValid(confirmPassword)) {
            mConfirmPasswordView.setError(getString(R.string.error_invalid_confirmPassword));
            focusView = mConfirmPasswordView;
            cancel = true;
        } else if (!confirmPassword.equals(password)) {
            mConfirmPasswordView.setError(getString(R.string.error_password_not_match));
            focusView = mConfirmPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email) || !isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }


        if (TextUtils.isEmpty(customerName)) {
            mCustomerNameView.setError(getString(R.string.error_required_name));
            focusView = mCustomerNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(contactNo) || contactNo.length() != 10) {
            mContactView.setError(getString(R.string.error_invalid_contact_number));
            focusView = mContactView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        }
        else
        {
            String requestUrl = getString(R.string.APIBaseURL) + getString(R.string.CheckDuplicateEntry);

            new RegisterService(context, new IUserServiceResponse() {
                @Override
                public void processFinish(String response) {
                    CheckDuplicateEntry(response);
                }
            }).CheckDuplicateEntry(mContactView.getText().toString(), mEmailView.getText().toString(), requestUrl);
        }
        return cancel;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private void CheckDuplicateEntry(String response)
    {
        View focusView=null;
        List<KeyValue> keyValues = Arrays.asList(gson.fromJson(response,KeyValue[].class));
        isDuplicateContact = false;
        isDuplicateEmail = false;

        try {
            for (KeyValue keyValue : keyValues) {
                if (keyValue.Value.equals("True")) {
                    if (keyValue.Key.equals("Contact")) {
                        isDuplicateContact = true;
                        cancel = true;
                    }
                    if (keyValue.Key.equals("Email")) {
                        isDuplicateEmail = true;
                        cancel = true;
                    }
                }
            }
        } catch (Exception ex) {
            Log.d("Duplicate entry", ex.getMessage());
        }

        if (isDuplicateContact) {
            mContactView.setError(getString(R.string.error_duplicate_contact));
            focusView = mContactView;
            cancel = true;
        }
        else if (isDuplicateEmail) {
            mEmailView.setError(getString(R.string.error_duplicate_email));
            focusView = mEmailView;
            cancel = true;
        }

        if(cancel)
        {
            focusView.requestFocus();
        }
        else
        {
            attemptRegister();
        }
    }
    private void AsyncTaskResult(UserDetails userDetails) {
        if (userDetails != null && userDetails.Id > 0) {
            userDetails.Password = user.Password;
            SharedPreferences settings = getSharedPreferences(getResources().getString(R.string.shared_preferences_name), 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("UserDetails", gson.toJson(userDetails));

            // Commit the edits!
            editor.commit();
            Intent intent = new Intent(this, MainActivity.class);

            startActivity(intent);
        } else {
            ConnectionDetector connectionObj = new ConnectionDetector(this);
            try {
                if (connectionObj.execute().get()) {
                    Toast.makeText(this, getString(R.string.error_register), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
                }
            } catch (Exception ex) {
                Log.d("Connection Detector", ex.getMessage());
            }

        }
    }

}