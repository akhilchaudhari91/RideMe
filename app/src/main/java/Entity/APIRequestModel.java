package Entity;

import android.content.Context;

import java.util.List;

import api.AsyncResponse;

/**
 * Created by akhil on 03-01-2016.
 */
public class APIRequestModel {

    public APIRequestModel()
    {
        this.ProgressDialogTitle="";
        this.ProgressDialogMessage="";
        this.DisplayProgressBar=true;
        this.SetAuthorizationHeader=true;
        this.SetOTPHeader=false;
    }

    public Context Context;
    public String ProgressDialogTitle;
    public String ProgressDialogMessage;
    public boolean DisplayProgressBar;
    public String RequestUrl;
    public String PostRequestObject;
    public boolean SetAuthorizationHeader;
    public boolean SetOTPHeader;
    public List<KeyValue> KeyValuePair;
    public String HttpVerb;
    public AsyncResponse Response;
    public int OTP;
}
