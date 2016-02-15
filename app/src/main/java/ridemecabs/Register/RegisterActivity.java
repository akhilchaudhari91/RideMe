package ridemecabs.Register;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ridemecabs.rideme.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import Entity.User;
import Infrastructure.ConnectionDetector;
import Infrastructure.MD5;
import Services.Enum.DuplicateEntryStatus;
import Services.Enum.EnumSharedPreferences;
import Services.Register.IUserServiceResponse;
import Services.Register.RegisterService;
import ridemecabs.Main.MainActivity;
import ridemecabs.SplashScreen.SplashScreenActivity;

public class RegisterActivity extends AppCompatActivity{

    User user = null;
    Context context;
    boolean cancel = false;
    DuplicateEntryStatus isDuplicateContact = DuplicateEntryStatus.NotVerified;
    DuplicateEntryStatus isDuplicateEmail = DuplicateEntryStatus.NotVerified;


    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private EditText mCustomerNameView;
    private EditText mContactView;

    final Gson gson = new Gson();

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

            final Button mEmailRegisterButton = (Button) findViewById(R.id.email_register_button);

            mConfirmPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.id.registerConfirmPassword || id == EditorInfo.IME_NULL) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                        CheckValidations();
                        return true;
                    }
                    return false;
                }
            });
            mContactView.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        isDuplicateContact = DuplicateEntryStatus.NotVerified;
                        CheckDuplicateContact(false);
                    }
                }
            });

            mEmailView.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        isDuplicateEmail = DuplicateEntryStatus.NotVerified;
                        CheckDuplicateEmail(false);
                    }
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

        try {
            int counter = 0;
            while (counter < 30 && ((isDuplicateContact == DuplicateEntryStatus.NotVerified || isDuplicateEmail == DuplicateEntryStatus.NotVerified))) {
                Thread.sleep(1000);
                counter++;
            }
        } catch (InterruptedException ex) {
            Log.d("Duplicate entry", ex.getMessage());
        }

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
            user.Password = MD5.crypt(password);

            String requestUrl = getString(R.string.APIBaseURL) + getString(R.string.RegisterUser);

            new RegisterService(context, new IUserServiceResponse() {
                @Override
                public void processFinish(String response) {
                    try {
                        if(!TextUtils.isEmpty(response)) {
                            JSONObject obj = new JSONObject(response);
                            user.AccessToken = obj.getString("accessToken").toString();
                            user.Id = Integer.parseInt(obj.getString("id").toString());
                        }

                        AsyncTaskResult(user);
                    } catch (JSONException ex) {
                        Log.d("Registration response: ", ex.getMessage());
                    }
                }
            }).RegisterUser(user, requestUrl);

        } catch (Exception ex) {
            Log.d("RegisterActivity", ex.getMessage());
        }
    }

    private boolean CheckValidations() {
        // Reset errors.
        mPasswordView.setError(null);
        mConfirmPasswordView.setError(null);
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

        if (isDuplicateEmail == DuplicateEntryStatus.True) {
            focusView = mEmailView;
            cancel = true;
        }
        if (isDuplicateContact == DuplicateEntryStatus.True) {
            focusView = mContactView;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            if (isDuplicateContact == DuplicateEntryStatus.Error || isDuplicateContact == DuplicateEntryStatus.NotVerified ||
                    isDuplicateEmail == DuplicateEntryStatus.Error || isDuplicateEmail == DuplicateEntryStatus.NotVerified) {
                CheckDuplicateContact(true);
                CheckDuplicateEmail(true);
            }
                attemptRegister();
        }
        return cancel;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private void AsyncTaskResult(User userDetails) {
        if (userDetails != null && userDetails.Id > 0) {
            userDetails.Password = user.Password;
            SharedPreferences settings = getSharedPreferences(EnumSharedPreferences.UserDetails.toString(), 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(EnumSharedPreferences.UserDetails.toString(), gson.toJson(userDetails));

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

    private void CheckDuplicateContact(boolean displayProgressBar) {
        String requestUrl = getString(R.string.APIBaseURL) + getString(R.string.CheckIfContactExists);
        new RegisterService(context, new IUserServiceResponse() {
            @Override
            public void processFinish(String response) {
                if (TextUtils.isEmpty(response)) {
                    isDuplicateContact = DuplicateEntryStatus.Error;
                } else {
                    isDuplicateContact = DuplicateEntryStatus.getEnum(Integer.parseInt(response));
                    if (isDuplicateContact == DuplicateEntryStatus.True) {
                        mContactView.setError(getString(R.string.error_duplicate_contact));
                    } else {
                        mContactView.setError(null);
                    }
                }
            }
        }).CheckIfContactExists(mContactView.getText().toString(), requestUrl, displayProgressBar);
    }

    private void CheckDuplicateEmail(boolean displayProgressBar) {
        String requestUrl = getString(R.string.APIBaseURL) + getString(R.string.CheckIfEmailExists);
        new RegisterService(context, new IUserServiceResponse() {
            @Override
            public void processFinish(String response) {
                if (TextUtils.isEmpty(response)) {
                    isDuplicateEmail = DuplicateEntryStatus.Error;
                } else {
                    isDuplicateEmail = DuplicateEntryStatus.getEnum(Integer.parseInt(response));
                    if (isDuplicateEmail == DuplicateEntryStatus.True) {
                        mEmailView.setError(getString(R.string.error_duplicate_email));
                    } else {
                        mEmailView.setError(null);
                    }
                }
            }
        }).CheckIfEmailExists(mEmailView.getText().toString(), requestUrl, displayProgressBar);
    }
}