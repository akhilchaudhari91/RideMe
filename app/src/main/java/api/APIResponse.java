package api;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpParser;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.gson.Gson;

import Entity.APIRequestModel;
import Entity.KeyValue;

/**
 * Created by akhil on 03-01-2016.
 */
public class APIResponse extends AsyncTask<APIRequestModel, Void, String> {

    Context context;
    String title;
    String message;
    static int progressCount = 0;

    private ProgressDialog mLoadingDialog;
    private Handler mHandler = new Handler();
    public AsyncResponse delegate = null;
    private final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    final Gson gson = new Gson();

    private void showLoadingDialog(final String title, final String msg) {
        progressCount++;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mLoadingDialog == null) {
                    mLoadingDialog = ProgressDialog.show(context, title, msg);
                }
                mLoadingDialog.setTitle(title);
                mLoadingDialog.setMessage(msg);
                mLoadingDialog.show();
            }
        });
    }

    private void hideLoadingDialog() {
        mHandler.post(new Runnable() { //Make sure it happens in sequence after showLoadingDialog
            @Override
            public void run() {
                if (mLoadingDialog != null && --progressCount==0) {
                    mLoadingDialog.dismiss();
                }
            }
        });
    }

    public APIResponse(Context context, AsyncResponse delegate, String title,String message)
    {
        this.context = context;
        this.delegate = delegate;
        this.title = title;
        this.message = message;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(!message.equals("")) {
            showLoadingDialog(title, message);
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        hideLoadingDialog();
        if(!message.equals("")) {
            delegate.processFinish(result);
        }
    }

    @Override
    protected String doInBackground(APIRequestModel... params) {
        String result = "";
        HttpRequest request=null;
        try {

            HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);

            if(params[0].HttpVerb.equals("GET")) {
                request = httpRequestFactory
                        .buildGetRequest(new GenericUrl(params[0].RequestUrl));
                request.getUrl().put("json", gson.toJson(params[0].KeyValuePair));
            }
            if(params[0].HttpVerb.equals("POST")) {
                request = httpRequestFactory
                        .buildPostRequest(new GenericUrl(params[0].RequestUrl), ByteArrayContent.fromString(null, params[0].PostRequestObject));

                request.getHeaders().setContentType("application/json");
            }

            result = request.execute().parseAsString();
        } catch (Exception e) {
            Log.e("APIResponse:", e.getMessage());
        }
        return result;
    }

    public HttpRequestFactory createRequestFactory(
            final HttpTransport transport) {
        return transport.createRequestFactory(new HttpRequestInitializer() {
            public void initialize(HttpRequest request) {
                GoogleHeaders headers = new GoogleHeaders();
                request.setHeaders(headers);
                JsonHttpParser parser = new JsonHttpParser(new JacksonFactory());
                request.addParser(parser);
            }
        });
    }
}