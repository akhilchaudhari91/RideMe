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

    public List<KeyValue> CheckDuplicateEntry(String contact, String email, String requestUrl) {
        keyValues = new ArrayList<>();
        keyValues.add(new KeyValue("Contact", contact));
        keyValues.add(new KeyValue("Email", email));

        apiRequestModel = new APIRequestModel();
        apiRequestModel.KeyValuePair = keyValues;
        apiRequestModel.RequestUrl = requestUrl;
        apiRequestModel.HttpVerb = "GET";

        try {
            asyncTask = new APIResponse(context, new AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    delegate.processFinish(output);
                }
            },"", "Checking duplicate entries");

            asyncTask.execute(apiRequestModel);


        } catch (Exception ex) {
            Log.d("Duplicate Entry: ", ex.getMessage());
        }
        return keyValues;
    }

    public void RegisterUser(User model, String requestUrl)
    {
        apiRequestModel = new APIRequestModel();
        apiRequestModel.PostRequestObject = gson.toJson(model);
        apiRequestModel.RequestUrl = requestUrl;
        apiRequestModel.HttpVerb = "POST";

        asyncTask =   new APIResponse(context, new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                delegate.processFinish(output);
            }
        },"","Creating your account");

        try {
            asyncTask.execute(apiRequestModel);
        } catch (Exception ex) {
            Log.d("User Registration: ", ex.getMessage());
        }
    }
}
