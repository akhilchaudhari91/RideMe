package ridemecabs.Main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.multidex.MultiDex;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ridemecabs.rideme.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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

import java.util.ArrayList;
import java.util.List;

import Entity.APIRequestModel;
import Entity.CabDuration;
import Entity.KeyValue;
import Entity.PlaceSearch.SearchPlacesModel;
import Entity.RideNowModel;
import Entity.UserDetails;
import Infrastructure.ConnectionDetector;
import Infrastructure.LocationHelper;
import Infrastructure.UIHelper;
import Services.Map.MapService;
import api.APIResponse;
import api.AsyncResponse;
import ridemecabs.ApplyCoupon.ListView;
import ridemecabs.Exception.BaseAppCompatActivity;

public class MainActivity extends BaseAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback {

    private GoogleMap mMap;
    LocationHelper locationHelper;
    CabDuration[] duration = null;
    boolean displayCabMarkers;
    RideNowModel rideNowDetails;
    Gson gson = new Gson();
    Marker myLocationMarker;

    Snackbar noLocationServiceSnackBar;
    Snackbar noInternetSnackBar;

    CoordinatorLayout noLocationSnackBarLayout;
    CoordinatorLayout noInternetSnackBarLayout;
    TextView txtViewCabDuration;
    ImageView imgViewMini;
    ImageView imgViewSedan;
    ImageView imgViewPrime;
    ImageView imgApplyCoupon;
    LinearLayout layoutImageView;
    LinearLayout layoutRide;
    LinearLayout layoutDriverDetails;
    TextView txtSearchPlaces;
    TextView txtRideEstimate;
    TextView txtViewETADuration;
    LinearLayout layoutCancelRide;
    LinearLayout layoutSearchAddress;

    Handler noConnectionHandler = new Handler();
    Runnable noConnectionRunnable;

    Handler cabDurationHandler = new Handler();
    Runnable cabDurationRunnable;

    Context context;
    String cabType;
    SearchPlacesHelper searchPlacesHelper;

    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base); MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            context = this;
            locationHelper = new LocationHelper(context);

            rideNowDetails= (RideNowModel)this.getIntent().getSerializableExtra("rideNowDetails");
            if(rideNowDetails==null) {
                rideNowDetails = new RideNowModel();
            }

            rideNowDetails.userDetails = gson.fromJson(this.getIntent().getSerializableExtra("UserDetails").toString(),UserDetails.class);

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            noInternetSnackBarLayout = (CoordinatorLayout) findViewById(R.id.noInternetSnackBarLayout);
            noLocationSnackBarLayout = (CoordinatorLayout) findViewById(R.id.noLocationSnackBarLayout);
            txtViewCabDuration = (TextView) findViewById(R.id.txtViewCabDuration);
            imgViewMini = (ImageView) findViewById(R.id.miniCabImage);
            imgViewSedan = (ImageView) findViewById(R.id.sedanCabImage);
            imgViewPrime = (ImageView) findViewById(R.id.primeCabImage);
            layoutImageView = (LinearLayout) findViewById(R.id.layoutImageView);
            imgApplyCoupon = (ImageView) findViewById(R.id.imgApplyCoupon);
            TextView txtFareEstimate = (TextView) findViewById(R.id.txtViewFareEstimate);
            txtSearchPlaces = (TextView) findViewById(R.id.txtSearchPlaces);
            txtRideEstimate = ((TextView)findViewById(R.id.txtViewFareEstimate));
            layoutDriverDetails = ((LinearLayout)findViewById(R.id.layoutDriverDetails));
            layoutRide = ((LinearLayout)findViewById(R.id.layoutRide));
            layoutCancelRide= ((LinearLayout)findViewById(R.id.layoutCancelRide));
            layoutSearchAddress= ((LinearLayout)findViewById(R.id.layoutSearchAddress));

            imgViewMini.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    displayCabMarkers(duration, "Mini");
                }
            });

            imgViewSedan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    displayCabMarkers(duration, "Sedan");
                }
            });

            imgViewPrime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    displayCabMarkers(duration, "Prime");
                }
            });

            imgApplyCoupon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ListView.class);
                    intent.putExtra("rideNowDetails",rideNowDetails);
                    startActivity(intent);
                }
            });

            txtFareEstimate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ridemecabs.Search.ListView.class);

                    rideNowDetails.CabType = cabType;

                    intent.putExtra("rideNowDetails", rideNowDetails);
                    intent.putExtra("calculateFareEstimate", true);
                    try {
                        startActivity(intent);
                    }
                    catch(Exception ex)
                    {
                        Log.d("Activity",ex.getMessage());
                    }
                }
            });

            txtSearchPlaces.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ridemecabs.Search.ListView.class);

                    rideNowDetails.CabType = cabType;

                    intent.putExtra("rideNowDetails", rideNowDetails);
                    intent.putExtra("calculateFareEstimate", false);
                    try {
                        startActivity(intent);
                    }
                    catch(Exception ex)
                    {
                        Log.d("Activity",ex.getMessage());
                    }
                }
            });


            CheckInternetConnectivity();
        }
        catch (Exception ex)
        {
            Log.d("h",ex.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);


        rideNowDetails= (RideNowModel)intent.getSerializableExtra("rideNowDetails");
        if(!TextUtils.isEmpty(rideNowDetails.SourceLocation.Address)) {
            txtSearchPlaces.setText(rideNowDetails.SourceLocation.Address);
        }

        if(rideNowDetails.SourceLocation.Latitude!=0 && rideNowDetails.SourceLocation.Longitude!=0) {
            UpdateCabDurations(cabType);
            if (myLocationMarker != null) {
                myLocationMarker.remove();
            }
            final LatLng latlng
                    = new LatLng(rideNowDetails.SourceLocation.Latitude, rideNowDetails.SourceLocation.Longitude);

            myLocationMarker = mMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation)));
            myLocationMarker.setVisible(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
            mMap.animateCamera(UIHelper.getUpdateCameraPosition(latlng));
        }
        if(rideNowDetails.RideEstimate.TotalFare>0)
        {
            txtRideEstimate.setText("₹"+ Double.toString(rideNowDetails.RideEstimate.TotalFare));
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_rideNow) {
            // Handle the camera action
        } else if (id == R.id.nav_myRides) {
//            setContentView(R.layout.activity_my_rides);

        } else if (id == R.id.nav_emergencyContact) {

        } else if (id == R.id.nav_about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setPadding(0, 0, 0, 450);
        try {
            mMap.setMyLocationEnabled(true);
        }
        catch (SecurityException ex)
        {
            Log.d("My Location",ex.getMessage());
        }

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (locationHelper.canGetLocation()) {
                    rideNowDetails.SourceLocation.SetLocationDetailsModel(locationHelper.getLocation());
                    new SearchPlacesHelper(Double.toString(locationHelper.getLatitude()) + "," + Double.toString(locationHelper.getLongitude()), new AsyncResponse() {
                        @Override
                        public void processFinish(String response) {
                            rideNowDetails.SourceLocation.Address = response;
                            txtSearchPlaces.setText(response);
                        }
                    }).execute();
                }

                if (rideNowDetails.SourceLocation.Latitude != 0 && rideNowDetails.SourceLocation.Longitude != 0) {
                    UpdateCabDurations(cabType);
                    if (myLocationMarker != null) {
                        myLocationMarker.remove();
                    }
                    final LatLng latlng
                            = new LatLng(rideNowDetails.SourceLocation.Latitude, rideNowDetails.SourceLocation.Longitude);

                    myLocationMarker = mMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation)));
                    myLocationMarker.setVisible(true);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                    mMap.animateCamera(UIHelper.getUpdateCameraPosition(latlng));
                }
                return false;
            }
        });

        if(locationHelper.canGetLocation())
        {
            if(rideNowDetails.SourceLocation.Latitude==0) {
                rideNowDetails.SourceLocation.SetLocationDetailsModel(locationHelper.getLocation());
            }

            if(rideNowDetails.SourceLocation.Latitude!=0) {
                final LatLng latlng
                        = new LatLng(rideNowDetails.SourceLocation.Latitude, rideNowDetails.SourceLocation.Longitude);

                new SearchPlacesHelper(Double.toString(locationHelper.getLatitude()) + "," + Double.toString(locationHelper.getLongitude()), new AsyncResponse() {
                    @Override
                    public void processFinish(String response) {
                        rideNowDetails.SourceLocation.Address = response;
                        txtSearchPlaces.setText(response);
                    }
                }).execute();

                if(myLocationMarker!=null) {
                    myLocationMarker.remove();
                }
                    myLocationMarker = mMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation)));
                    myLocationMarker.setVisible(true);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                mMap.animateCamera(UIHelper.getUpdateCameraPosition(latlng));
                UpdateCabDurations("Mini");
            }
        }
    }

    private void CheckInternetConnectivity()
    {
        noConnectionRunnable = new Runnable() {
            public void run() {
                ShowInternetConnectionSnackBar();
            }
        };
        noConnectionRunnable.run();
    }

    private void ShowInternetConnectionSnackBar()
    {
        try {
            if (!new ConnectionDetector(this).execute().get()) {
                displayCabMarkers=true;
                if (noInternetSnackBar == null) {
                    layoutImageView.setVisibility(View.INVISIBLE);
                    noInternetSnackBarLayout.setVisibility(View.VISIBLE);
                    noInternetSnackBar = createSnackBar(noInternetSnackBarLayout, "No Internet Connection", noInternetSnackBar,false);
                }
                if (noLocationServiceSnackBar != null) {
                    noLocationServiceSnackBar.dismiss();
                    noLocationSnackBarLayout.setVisibility(View.GONE);
                    noLocationServiceSnackBar = null;
                }

            } else
            {
                if (noInternetSnackBar != null) {
                    noInternetSnackBar.dismiss();
                    noInternetSnackBar = null;
                    noInternetSnackBarLayout.setVisibility(View.GONE);
                }
                if(!locationHelper.canGetLocation())
                {
                    if(!displayCabMarkers) {
                        if (noLocationServiceSnackBar != null) {
                            noLocationServiceSnackBar.dismiss();
                            noLocationSnackBarLayout.setVisibility(View.GONE);
                            noLocationServiceSnackBar = null;
                        }
                    }
                    noLocationSnackBarLayout.setVisibility(View.VISIBLE);
                    layoutImageView.setVisibility(View.INVISIBLE);
                    noLocationServiceSnackBar = createSnackBar(noLocationSnackBarLayout,"Enable Location Services", noLocationServiceSnackBar,true);
                    displayCabMarkers=true;
                }
                else
                {
                    if(rideNowDetails.RideStatus==0 || rideNowDetails.RideStatus==1) {
                        layoutCancelRide.setVisibility(View.GONE);
                        layoutSearchAddress.setVisibility(View.VISIBLE);
                        if (displayCabMarkers) {
                            if (noLocationServiceSnackBar != null) {
                                noLocationServiceSnackBar.dismiss();
                                noLocationSnackBarLayout.setVisibility(View.GONE);
                                noLocationServiceSnackBar = null;
                            }
                            displayCabMarkers(duration, cabType);
                            displayCabMarkers = false;
                        }
                        layoutImageView.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
        catch(Exception ex)
        {
            Log.d("Connection Detector", ex.getMessage());
        }
        noConnectionHandler.postDelayed(noConnectionRunnable, 5000);
    }

    private Snackbar createSnackBar(CoordinatorLayout layout,String messageText, Snackbar existingSnackBar, boolean includeEnableAction)
    {
        Snackbar snackBar;
        if(existingSnackBar==null) {
            snackBar = Snackbar.make(layout, "", Snackbar.LENGTH_INDEFINITE);
        }
        else
        {
            snackBar=existingSnackBar;
        }

        if(includeEnableAction) {
            snackBar.setAction("ENABLE", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
        }
        View sbView = snackBar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setText(messageText);
        textView.setTextColor(Color.YELLOW);
        layout.setVisibility(View.VISIBLE);
        snackBar.show();
        ((android.support.design.widget.CoordinatorLayout.LayoutParams) snackBar.getView().getLayoutParams()).setBehavior(null);

        return  snackBar;
    }

    private void expandOrCollapse(final View v,String exp_or_colpse) {
        TranslateAnimation anim;
        if(exp_or_colpse.equals("expand"))
        {
            anim = new TranslateAnimation(0.0f, 0.0f,-v.getHeight(), 0.0f);
            v.setVisibility(View.VISIBLE);

            anim.setDuration(750);
            anim.setInterpolator(new AccelerateInterpolator(0.5f));
            v.startAnimation(anim);
        }
        else{
            anim = new TranslateAnimation(0.0f, 0.0f, 0.0f,-v.getHeight());
            Animation.AnimationListener collapselistener= new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    v.setVisibility(View.GONE);
                }
            };

            anim.setAnimationListener(collapselistener);

            anim.setDuration(750);
            anim.setInterpolator(new AccelerateInterpolator(0.5f));
            v.startAnimation(anim);
        }
    }

    private void UpdateCabDurations(final String cabType)
    {
        try {
            cabDurationRunnable = new Runnable() {
                public void run() {
                    getCabDurations(cabType,rideNowDetails.Driver.DriverId);

                }
            };
            cabDurationRunnable.run();
        }
        catch (Exception ex)
        {
            Log.d("f", ex.getMessage());
        }
    }

    private void getCabDurations(final String cabType, final int driverId)
    {
        try {
            String requestUrl = getString(R.string.APIBaseURL) + getString(R.string.GetCabDuration);
            //duration = mapService.UpdateCabDuration(requestUrl, location.getLatitude(), location.getLongitude());
            APIRequestModel apiRequestModel;
            APIResponse asyncTask;
            List<KeyValue> keyValues;
            Gson gson;

            CabDuration[] cabDurations = null;
            keyValues = new ArrayList<KeyValue>();
            keyValues.add(new KeyValue("latitude",  Double.toString(rideNowDetails.SourceLocation.Latitude)));
            keyValues.add(new KeyValue("longitude",  Double.toString(rideNowDetails.SourceLocation.Longitude)));
            keyValues.add(new KeyValue("driverId",  Integer.toString(driverId)));

            apiRequestModel = new APIRequestModel();
            apiRequestModel.KeyValuePair = keyValues;
            apiRequestModel.RequestUrl = requestUrl;
            apiRequestModel.HttpVerb="GET";

            asyncTask = new APIResponse(context, null,"","");

            try {
                String APIResponse = asyncTask.execute(apiRequestModel).get();
                gson = new Gson();
                duration = gson.fromJson(APIResponse, CabDuration[].class);
            } catch (Exception ex) {
                Log.d("Cab Duration: ", ex.getMessage());
            }

            displayCabMarkers(duration, cabType);
        }
        catch(Exception ex)
        {
            Log.d("GetCabDurations", ex.getMessage());
        }
        cabDurationHandler.postDelayed(cabDurationRunnable, 30000);
    }

    private void displayCabMarkers(CabDuration[] cabDurations, String cabType)
    {
        this.cabType = cabType==null?"Mini":cabType;
        if (cabDurations != null && duration.length>0) {
            for (CabDuration cab : cabDurations) {
                if (cab.CarType.equals(cabType)) {
                    mMap.clear();
                    final LatLng latlng
                            = new LatLng(rideNowDetails.SourceLocation.Latitude, rideNowDetails.SourceLocation.Longitude);
                    if(myLocationMarker!=null)
                    {
                     myLocationMarker.remove();
                    }
                        myLocationMarker = mMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation)));
                        myLocationMarker.setVisible(true);
                    for (String location : cab.Drivers) {
                        mMap.addMarker(new MarkerOptions().position(UIHelper.GetLatLngPosition(location)).title(cab.CarType).icon(BitmapDescriptorFactory.fromResource(R.drawable.mini)));
                    }
                        if (rideNowDetails.RideStatus == 0 || rideNowDetails.RideStatus == 1) {
                            if (cab.DurationValue != 0) {
                            txtViewCabDuration.setText(cab.DurationText);
                            layoutImageView.setVisibility(View.VISIBLE);
                            if (noLocationServiceSnackBar != null) {
                                noLocationServiceSnackBar.dismiss();
                                noLocationServiceSnackBar = null;
                            }
                        } else {
                            txtViewCabDuration.setText("No cabs");
                        }
                    }
                }
            }
        }
        else {
            txtViewCabDuration.setText("No cabs");
        }
    }

    public void ShowConfirmation(View view){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Confirm your ride");
        alertDialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            String requestUrl = getString(R.string.APIBaseURL) + getString(R.string.ConfirmRide);
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                new MapService(context, new AsyncResponse() {
                    @Override
                    public void processFinish(String response) {
                        //rideNowDetails = gson.fromJson(response,RideNowModel.class);
                        rideNowDetails.RideStatus=2;
                        UpdateUI(2);
                    }
                }).ConfirmRide(requestUrl, rideNowDetails);
            }
        });

        alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void UpdateUI(int rideStatus)
    {
        if(rideStatus==2)
        {
            noConnectionHandler.removeCallbacks(noConnectionRunnable);
            getCabDurations(cabType, rideNowDetails.Driver.DriverId);
            layoutImageView.setVisibility(View.GONE);
            layoutRide.setVisibility(View.GONE);
            layoutDriverDetails.setVisibility(View.VISIBLE);
            txtViewETADuration=((TextView)findViewById(R.id.txtViewETADuration));
            layoutCancelRide.setVisibility(View.VISIBLE);
            layoutSearchAddress.setVisibility(View.GONE);
            if(TextUtils.isEmpty(rideNowDetails.ETA))
            {
                txtViewETADuration.setText("N/A");
            }
            else {
                txtViewETADuration.setText(rideNowDetails.ETA);
            }
            (findViewById(R.id.btnCallDriver)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phno = rideNowDetails.userDetails.Contact;

                    Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phno));
                    startActivity(i);
                }
            });
        }
    }
}

class SearchPlacesHelper extends AsyncTask<Void, Void, String> {

    /** Global instance of the HTTP transport. */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    private String latlng;
    SearchPlacesModel list;
    String address;
    private static final String API_URL = "https://maps.googleapis.com/maps/api/geocode/json?";
    public AsyncResponse delegate = null;
    public SearchPlacesHelper(String latlng,AsyncResponse delegate) {
        this.latlng = latlng;
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(Void... params)
    {

        try {

            HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);
            HttpRequest request = httpRequestFactory
                    .buildGetRequest(new GenericUrl(API_URL));
            request.getUrl().put("key", "AIzaSyAM6e58t0Fe-D4zAy6JeHOQ__YjA5BErog");
            request.getUrl().put("latlng", latlng);

            String s = request.execute().parseAsString();

            Gson gson = new Gson();
            list = gson.fromJson(s, SearchPlacesModel.class);
            address = list.results.get(0).formatted_address;
        } catch (Exception e) {
            Log.e("Error:", e.getMessage());
            //    return null;
        }
        return address;
    }


    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
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
