package Services.Register;

import android.content.Context;
import android.util.Log;

import com.example.ridemecabs.rideme.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import Entity.APIRequestModel;
import Entity.KeyValue;
import Entity.User;
import api.APIResponse;
import api.AsyncResponse;

/**
 * Created by akhil on 04-01-2016.
 */
public class RegisterService {

    APIRequestModel apiRequestModel;
    static APIResponse asyncTaskContact;
    static APIResponse asyncTaskEmail;
    APIResponse asyncTask;
    List<KeyValue> keyValues;
    final Gson gson = new Gson();
    Context context;

    IUserServiceResponse delegate;

    public RegisterService(Context context, IUserServiceResponse delegate)
    {
        this.context = context;
        this.delegate = delegate;
    }

    public List<KeyValue> CheckIfEmailExists(String email, String requestUrl, boolean displayProgressBar) {
        keyValues = new ArrayList<>();
        keyValues.add(new KeyValue("Email", email));

        apiRequestModel = new APIRequestModel();
        apiRequestModel.Context = context;
        apiRequestModel.KeyValuePair = keyValues;
        apiRequestModel.RequestUrl = requestUrl;
        apiRequestModel.HttpVerb = "GET";
        apiRequestModel.SetAuthorizationHeader=false;
        apiRequestModel.SetOTPHeader=false;
        apiRequestModel.DisplayProgressBar=displayProgressBar;
        apiRequestModel.ProgressDialogMessage="Validating";
        apiRequestModel.Response=new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                delegate.processFinish(output);
            }
        };

        try {
            if(asyncTaskEmail!=null)
            {
                asyncTaskEmail.cancel(true);
            }
            asyncTaskEmail = new APIResponse(apiRequestModel);

            asyncTaskEmail.execute();


        } catch (Exception ex) {
            Log.d("Duplicate Email: ", ex.getMessage());
        }
        return keyValues;
    }

    public List<KeyValue> CheckIfContactExists(String contact, String requestUrl, boolean displayProgressBar) {
        keyValues = new ArrayList<>();
        keyValues.add(new KeyValue("Contact", contact));

        apiRequestModel = new APIRequestModel();
        apiRequestModel.Context = context;
        apiRequestModel.KeyValuePair = keyValues;
        apiRequestModel.RequestUrl = requestUrl;
        apiRequestModel.HttpVerb = "GET";
        apiRequestModel.SetAuthorizationHeader=false;
        apiRequestModel.SetOTPHeader=false;
        apiRequestModel.DisplayProgressBar=displayProgressBar;
        apiRequestModel.ProgressDialogMessage="Validating";
        apiRequestModel.Response=new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                delegate.processFinish(output);
            }
        };

        try {
            if(asyncTaskContact!=null)
            {
                asyncTaskContact.cancel(true);
            }
            asyncTaskContact = new APIResponse(apiRequestModel);

            asyncTaskContact.execute();


        } catch (Exception ex) {
            Log.d("Duplicate Contact: ", ex.getMessage());
        }
        return keyValues;
    }

    public void RegisterUser(User model, String requestUrl)
    {
        apiRequestModel = new APIRequestModel();
        apiRequestModel.Context = context;
        apiRequestModel.PostRequestObject = gson.toJson(model);
        apiRequestModel.RequestUrl = requestUrl;
        apiRequestModel.HttpVerb = "POST";
        apiRequestModel.SetAuthorizationHeader=false;
        apiRequestModel.SetOTPHeader=false;
        apiRequestModel.ProgressDialogTitle="";
        apiRequestModel.ProgressDialogMessage="Creating account";
        apiRequestModel.DisplayProgressBar=true;
        apiRequestModel.Response= new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                delegate.processFinish(output);

            }
        };

        asyncTask = new APIResponse(apiRequestModel);

        try {
            asyncTask.execute();
        } catch (Exception ex) {
            Log.d("User Registration: ", ex.getMessage());
        }
    }
}
