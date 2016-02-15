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
import com.google.api.client.http.HttpHeaders;
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
public class APIResponse extends AsyncTask<Void, Void, String> {

    final APIRequestModel model;
    static int progressCount = 0;
    static String AuthorizationToken;

    private ProgressDialog mLoadingDialog;
    private Handler mHandler = new Handler();
    private final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    final Gson gson = new Gson();

    private void showLoadingDialog() {
        progressCount++;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mLoadingDialog == null) {
                    mLoadingDialog = ProgressDialog.show(model.Context, model.ProgressDialogTitle,model.ProgressDialogMessage);
                }
                mLoadingDialog.setTitle(model.ProgressDialogTitle);
                mLoadingDialog.setMessage(model.ProgressDialogMessage);
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

    public APIResponse(APIRequestModel model)
    {
        this.model = model;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(model.DisplayProgressBar) {
            showLoadingDialog();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        hideLoadingDialog();
        if(model.Response!=null) {
            model.Response.processFinish(result);
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        String result = "";
        HttpRequest request=null;
        try {

            HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);

            if(model.HttpVerb.equals("GET")) {
                request = httpRequestFactory
                        .buildGetRequest(new GenericUrl(model.RequestUrl));
                request.getUrl().put("json", gson.toJson(model.KeyValuePair));
                if(model.SetAuthorizationHeader) {
                    request.getHeaders().setAuthorization(AuthorizationToken);
                }
            }
            if(model.HttpVerb.equals("POST")) {
                request = httpRequestFactory
                        .buildPostRequest(new GenericUrl(model.RequestUrl), ByteArrayContent.fromString(null, model.PostRequestObject));

                request.getHeaders().setContentType("application/json");
                if(model.SetAuthorizationHeader) {
                    request.getHeaders().setAccept("application/json");
                    request.getHeaders().setAuthorization(AuthorizationToken);
                }
                if(model.SetOTPHeader) {
                    HttpHeaders headers = new HttpHeaders();
                    request.getHeaders().setAccept("application/json");
                    headers.set("X-OTP",model.OTP);
                }
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