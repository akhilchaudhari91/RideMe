package Infrastructure;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpParser;
import com.google.api.client.json.jackson.JacksonFactory;

import java.io.IOException;

import api.GetAPIRequestFactoryObject;

@SuppressWarnings("deprecation")
public class ConnectionDetector extends AsyncTask<Void, Void, Boolean> {

    /** Global instance of the HTTP transport. */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final String URL = "http://www.google.com";

    private Context _context;

    public ConnectionDetector(Context context){
        this._context = context;
    }

    @Override
    protected Boolean doInBackground(Void... params)
    {

        try {

            ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null)
            {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null)
                    for (int i = 0; i < info.length; i++)
                        if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        {
                            try
                            {
                                HttpRequestFactory httpRequestFactory = GetAPIRequestFactoryObject.createRequestFactory(HTTP_TRANSPORT);
                                HttpRequest request = httpRequestFactory
                                        .buildGetRequest(new GenericUrl(URL));
                                int responseCode = request.execute().getStatusCode();

                                return responseCode==200;

                            } catch (IOException e)
                            {
                                return (false);
                            }
                        }

            }
            return false;
        } catch (Exception e) {
            Log.e("Error:", e.getMessage());
        }
        return true;
    }

}