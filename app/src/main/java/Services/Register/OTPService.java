package Services.Register;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.util.List;

import Entity.APIRequestModel;
import Entity.KeyValue;
import Entity.User;
import api.APIResponse;
import api.AsyncResponse;

/**
 * Created by akhil on 20-02-2016.
 */
public class OTPService {
    private Context context;
    private AsyncResponse delegate;
    APIRequestModel apiRequestModel;
    APIResponse asyncTask;
    List<KeyValue> keyValues;
    final Gson gson = new Gson();

    public OTPService(Context context, AsyncResponse delegate)
    {
        this.context = context;
        this.delegate = delegate;
    }

    public void VerifyOTP(String accessToken,int id, int otp , String requestUrl)
    {
        apiRequestModel = new APIRequestModel();
        apiRequestModel.Context = context;
        apiRequestModel.PostRequestObject = "";
        apiRequestModel.RequestUrl = requestUrl + "?json=" + id;
        apiRequestModel.HttpVerb = "POST";
        apiRequestModel.SetAuthorizationHeader=true;
        apiRequestModel.SetOTPHeader=true;
        apiRequestModel.ProgressDialogTitle="";
        apiRequestModel.ProgressDialogMessage="Verifying";
        apiRequestModel.DisplayProgressBar=true;
        APIResponse.AuthorizationToken = accessToken;
        apiRequestModel.OTP = otp;
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
