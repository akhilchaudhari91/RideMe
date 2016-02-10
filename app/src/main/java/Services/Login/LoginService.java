package Services.Login;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;

import Entity.APIRequestModel;
import Entity.KeyValue;
import Entity.UserDetails;
import Services.Register.IUserServiceResponse;
import api.APIResponse;
import api.AsyncResponse;

/**
 * Created by akhil on 19-01-2016.
 */
public class LoginService {
    APIResponse asyncTask;
    APIRequestModel apiRequestModel = new APIRequestModel();
    final Gson gson = new Gson();
    Context context;

    IUserServiceResponse delegate;

    public LoginService(Context context, IUserServiceResponse delegate)
    {
        this.context = context;
        this.delegate = delegate;
    }

    public void AuthenticateUser(String email, String password, String requestUrl)
    {
        apiRequestModel.HttpVerb="GET";
        apiRequestModel.RequestUrl= requestUrl;
        apiRequestModel.KeyValuePair = new ArrayList<>();
        apiRequestModel.KeyValuePair.add(new KeyValue("Username",email));
        apiRequestModel.KeyValuePair.add(new KeyValue("Password",password));

        asyncTask =   new APIResponse(context, new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                delegate.processFinish(output);
            }
        },"","Logging in");

        try {
            asyncTask.execute(apiRequestModel);
        } catch (Exception ex) {
            Log.d("Login: ", ex.getMessage());
        }


    }
}
