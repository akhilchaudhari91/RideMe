package Services.Map;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Entity.APIRequestModel;
import Entity.CabDuration;
import Entity.KeyValue;
import Entity.RideNowModel;
import api.APIResponse;
import api.AsyncResponse;

/**
 * Created by akhil on 10-01-2016.
 */
public class MapService {

    APIRequestModel apiRequestModel;
    APIResponse asyncTask;
    List<KeyValue> keyValues;
    final Gson gson = new Gson();
    Context context;
    AsyncResponse delegate;

    public MapService(Context context, AsyncResponse delegate)
    {
        this.delegate=delegate;
        this.context = context;
    }

    public CabDuration[] UpdateCabDuration(String requestUrl, double latitude, double longitude)
    {
        CabDuration[] cabDurations = null;
        keyValues = new ArrayList<KeyValue>();
        keyValues.add(new KeyValue("latitude",  Double.toString(latitude)));
        keyValues.add(new KeyValue("longitude",  Double.toString(longitude)));

        apiRequestModel = new APIRequestModel();
        apiRequestModel.KeyValuePair = keyValues;
        apiRequestModel.RequestUrl = requestUrl;

        asyncTask = new APIResponse(context,null,"","");

        try {
            String APIResponse = asyncTask.execute(apiRequestModel).get();
            cabDurations = gson.fromJson(APIResponse, CabDuration[].class);
        } catch (Exception ex) {
            Log.d("Cab Duration: ", ex.getMessage());
        }
        return cabDurations;
    }

    public void ConfirmRide(String requestUrl, RideNowModel rideNowModel)
    {
        apiRequestModel = new APIRequestModel();
        apiRequestModel.PostRequestObject = gson.toJson(rideNowModel);
        apiRequestModel.RequestUrl = requestUrl;
        apiRequestModel.HttpVerb = "POST";

        asyncTask =   new APIResponse(context, new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                delegate.processFinish(output);
            }
        },"","Booking your ride");

        try {
            asyncTask.execute(apiRequestModel);
        } catch (Exception ex) {
            Log.d("ConfirmRide: ", ex.getMessage());
        }
    }
}
