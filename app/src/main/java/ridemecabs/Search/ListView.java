package ridemecabs.Search;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ridemecabs.rideme.R;
import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpParser;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;

import Entity.PlaceSearch.Result;
import Entity.PlaceSearch.SearchPlacesModel;
import Entity.RideNowModel;
import ridemecabs.Main.MainActivity;
import ridemecabs.RideEstimate.RideEstimateActivity;

public class ListView extends AppCompatActivity {
    RideNowModel rideDetails;
    Intent intent;
    android.widget.ListView listView;
    SearchPlacesHelper searchPlacesHelper;
    Context context;
    boolean calculateFareEstimate=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.searchplaceslist_main);
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.searchPlacesToolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        catch (Exception ex)
        {
            Log.d("ActionBar",ex.getMessage());
        }

        intent = getIntent();
        rideDetails= (RideNowModel)intent.getSerializableExtra("rideNowDetails");
        calculateFareEstimate = (boolean)intent.getSerializableExtra("calculateFareEstimate");


        final EditText txtSearchPlaces = (EditText) findViewById(R.id.txtSearchPlaces_popup);
        listView = (android.widget.ListView)findViewById(R.id.searchPlacesList);


        listView =  (android.widget.ListView) findViewById(R.id.serachPlaceslist);

        txtSearchPlaces.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.txtSearchPlaces_popup || id == EditorInfo.IME_ACTION_SEARCH || id == EditorInfo.IME_NULL) {
                    searchPlacesHelper = new SearchPlacesHelper(context, listView, txtSearchPlaces.getText().toString());
                    try {
                        searchPlacesHelper.execute();
                    } catch (Exception ex) {
                        Log.d("GerSearchPlaces", ex.getMessage());
                    }
                }
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (calculateFareEstimate == true) {
                    rideDetails.DestinationLocation.Latitude = ((Result) listView.getAdapter().getItem(position)).geometry.location.lat;
                    rideDetails.DestinationLocation.Longitude = ((Result) listView.getAdapter().getItem(position)).geometry.location.lng;
                    rideDetails.DestinationLocation.Address = ((Result) listView.getAdapter().getItem(position)).formatted_address;

                    intent = new Intent(getBaseContext(), RideEstimateActivity.class);
                    intent.putExtra("rideNowDetails", rideDetails);
                    startActivity(intent);

                } else {
                    rideDetails.SourceLocation.Latitude = ((Result) listView.getAdapter().getItem(position)).geometry.location.lat;
                    rideDetails.SourceLocation.Longitude = ((Result) listView.getAdapter().getItem(position)).geometry.location.lng;
                    rideDetails.SourceLocation.Address = ((Result) listView.getAdapter().getItem(position)).formatted_address;
                    rideDetails.searchAddress = ((Result) listView.getAdapter().getItem(position)).formatted_address;

                    intent = new Intent(getBaseContext(), MainActivity.class);
                    intent.putExtra("rideNowDetails", rideDetails);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });

    }
    @Override
    public void onBackPressed() {
        intent = new Intent(getBaseContext(), MainActivity.class);
        intent.putExtra("rideNowDetails", (Serializable)rideDetails);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setListView(String query)
    {
        searchPlacesHelper = new SearchPlacesHelper(this,listView,query);

        // setup the data source
        try {
            searchPlacesHelper.execute();
        }
        catch(Exception ex)
        {
            Log.d("GetUserCoupons", ex.getMessage());
        }
    }

}

class SearchPlacesHelper extends AsyncTask<Void, Void, SearchPlacesModel> {

    /** Global instance of the HTTP transport. */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    private String query;

    private static final String API_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json?";
    Context context;
    private SearchPlacesModel list;
    android.widget.ListView listView;
    private ArrayList<Result> data;
    public SearchPlacesHelper(Context context,android.widget.ListView listView,String query) {
        this.context=context;
        this.query = query;
        this.listView = listView;
    }

    @Override
    protected SearchPlacesModel doInBackground(Void... params)
    {

        try {

            HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);
            HttpRequest request = httpRequestFactory
                    .buildGetRequest(new GenericUrl(API_URL));
            request.getUrl().put("key", "AIzaSyAM6e58t0Fe-D4zAy6JeHOQ__YjA5BErog");
            request.getUrl().put("query", query);

            String s = request.execute().parseAsString();

            Gson gson = new Gson();
            list = gson.fromJson(s, SearchPlacesModel.class);
        } catch (Exception e) {
            Log.e("Error:", e.getMessage());
            //    return null;
        }
        return list;
    }


    @Override
    protected void onPostExecute(SearchPlacesModel feed) {

        data = new ArrayList<Result>();
        // setup the data adaptor
        this.data.addAll(feed.results);
        // setup the data adaptor
        CustomAdapter adapter = new CustomAdapter(context, R.layout.searchplaceslist_listitem, this.data);

        // specify the list adaptor
        //setListAdapter(adapter);

        listView.setAdapter(adapter);

        listView.setTextFilterEnabled(true);

    }

    /**
     * Creating http request Factory
     * */
    public static HttpRequestFactory createRequestFactory(
            final HttpTransport transport) {
        return transport.createRequestFactory(new HttpRequestInitializer() {
            public void initialize(HttpRequest request) {
                GoogleHeaders headers = new GoogleHeaders();
                headers.setApplicationName("AndroidHive-Places-Test");
                request.setHeaders(headers);
                JsonHttpParser parser = new JsonHttpParser(new JacksonFactory());
                request.addParser(parser);
            }
        });
    }

}