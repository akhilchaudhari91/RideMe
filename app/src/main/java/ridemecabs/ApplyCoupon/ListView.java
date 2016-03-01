package ridemecabs.ApplyCoupon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ridemecabs.rideme.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Entity.APIRequestModel;
import Entity.Coupon;
import Entity.KeyValue;
import Entity.RideNowModel;
import Infrastructure.ConnectionDetector;
import Services.Enum.EnumSharedPreferences;
import api.APIResponse;
import api.AsyncResponse;
import ridemecabs.Exception.BaseAppCompatActivity;
import ridemecabs.Main.MainActivity;

public class ListView extends BaseAppCompatActivity {
    RideNowModel rideDetails;
    Intent intent;
    android.widget.ListView listView;
    APIResponse asyncTask;
    Context context;
    APIRequestModel apiRequestModel = new APIRequestModel();
    List<KeyValue> keyValues;
    Gson gson = new Gson();
    List<Coupon> details=null;


    public void onCreate(Bundle savedInstanceState) {
        context = this;
        intent = getIntent();
        rideDetails = (RideNowModel) intent.getSerializableExtra(EnumSharedPreferences.RideNowModel.toString());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_coupons);
        listView = (android.widget.ListView) findViewById(R.id.list);
        final ImageView imgNoInternet = (ImageView) findViewById(R.id.imgNoInternet);
        final EditText txtApplyCoupon = (EditText) findViewById(R.id.txtApplyCoupon);


        imgNoInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchCoupons();
            }
        });

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.applyCouponToolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        catch (Exception ex)
        {
            Log.d("ActionBar",ex.getMessage());
        }

        txtApplyCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Coupon> searchModel = new ArrayList<Coupon>();
                for (Coupon model: details)
                {
                    if(model.CouponCode.contains(txtApplyCoupon.getText()) || model.Title.contains(txtApplyCoupon.getText()))
                    {
                        searchModel.add(model);
                    }
                    DisplayCoupons(searchModel);
                }
            }
        });

        SearchCoupons();

        // setup the data source

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                removeAllChecks((ViewGroup) view.getParent());
                if(!((Coupon) listView.getAdapter().getItem(position)).isSelected==true) {
                    listView.setSelection(position);

                    for (int i = 0; i < ((ViewGroup) view).getChildCount(); ++i) {
                        View nextChild = (((ViewGroup) view).getChildAt(i));
                        View imageView = ((ViewGroup) nextChild).getChildAt(0);

                        if (imageView instanceof ImageView) {
                            ((ImageView) imageView).setVisibility(ImageView.VISIBLE);
                            view.requestFocus();
                        }
                    }

                    if (rideDetails != null) {
                        listView.getAdapter().getItem(position);
                        rideDetails.SelectedCoupon = ((Coupon) listView.getAdapter().getItem(position));
                    }

                    // When clicked, show a toast with the TextView text
                    Toast.makeText(getApplicationContext(), "Coupon Applied !!!",
                            Toast.LENGTH_SHORT).show();

                    intent = new Intent(getBaseContext(), MainActivity.class);
                    intent.putExtra(EnumSharedPreferences.RideNowModel.toString(), rideDetails);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                ((Coupon) listView.getAdapter().getItem(position)).isSelected=false;
                if (rideDetails != null) {
                    rideDetails.SelectedCoupon = new Coupon();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        intent = new Intent(getBaseContext(), MainActivity.class);
        intent.putExtra("rideNowDetails", rideDetails);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void removeAllChecks(ViewGroup view) {
        View imageView;

        for (int i = 0; i < view.getChildCount(); i++) {
            try {

                imageView = ((ViewGroup) ((ViewGroup) view.getChildAt(i)).getChildAt(0)).getChildAt(0);
                imageView.setVisibility(ImageView.INVISIBLE);
            } catch (Exception e1) {
                continue;
            }
        }
    }

    private List<Coupon> setSelectedItem(List<Coupon> model, int couponId) {
        for (Coupon couponModel : model) {
            if (couponModel.Id == couponId) {
                couponModel.isSelected = true;
            } else {
                couponModel.isSelected = false;
            }
        }
        return model;
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

    private void SearchCoupons()
    {
        keyValues = new ArrayList<KeyValue>();
        keyValues.add(new KeyValue("userId", String.valueOf(rideDetails.userDetails.Id)));
        apiRequestModel.RequestUrl = getString(R.string.APIBaseURL) + getString(R.string.ApplyCoupon);
        apiRequestModel.KeyValuePair = keyValues;
        apiRequestModel.HttpVerb = "GET";
        apiRequestModel.Context = context;
        apiRequestModel.SetAuthorizationHeader=true;
        apiRequestModel.SetOTPHeader=false;
        apiRequestModel.DisplayProgressBar=true;
        apiRequestModel.ProgressDialogTitle="";
        apiRequestModel.ProgressDialogMessage="Fetching coupons";
        apiRequestModel.Response = new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                try {
                    Coupon[] couponDetails = gson.fromJson(output, Coupon[].class);
                    details = Arrays.asList(couponDetails);
                    DisplayCoupons(details);
                } catch (Exception ex) {
                    Log.d("GetUserCoupons", ex.getMessage());
                }
            }
        };
        asyncTask = new APIResponse(apiRequestModel);
        asyncTask.execute();
    }

    private void DisplayCoupons(List<Coupon> details)
    {
        final TextView emptyListMessageView = (TextView) findViewById(R.id.emptyCouponView);
        final LinearLayout layoutNoInternet = (LinearLayout) findViewById(R.id.layoutNoInternet);

        ArrayList<Coupon> data = new ArrayList<>();
        try {
            layoutNoInternet.setVisibility(View.GONE);
            CustomAdapter adapter;
            if (details.size() == 0) {
                emptyListMessageView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            }
            else {
                adapter = new CustomAdapter(context, R.layout.list_item, data);
                data.addAll(setSelectedItem(details, rideDetails.SelectedCoupon.Id));
                // setup the data adaptor

                listView.setAdapter(adapter);

                listView.setTextFilterEnabled(true);

                emptyListMessageView.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }
        } catch (Exception ex) {
            Log.d("GetUserCoupons", ex.getMessage());
            ConnectionDetector connectionObj = new ConnectionDetector(context);
            try {
                if (connectionObj.execute().get()) {
                    Toast.makeText(context, getString(R.string.error_applycoupon), Toast.LENGTH_LONG).show();
                } else {
                    listView.setVisibility(View.GONE);
                    emptyListMessageView.setVisibility(View.GONE);
                    layoutNoInternet.setVisibility(View.VISIBLE);
                }
            } catch (Exception ex2) {
                Log.d("Connection Detector", ex2.getMessage());
            }
        }
    }
}