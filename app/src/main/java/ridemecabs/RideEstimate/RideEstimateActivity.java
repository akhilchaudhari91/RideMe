package ridemecabs.RideEstimate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ridemecabs.rideme.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import Entity.APIRequestModel;
import Entity.KeyValue;
import Entity.RideNowModel;
import api.APIResponse;
import api.AsyncResponse;
import ridemecabs.Main.MainActivity;

public class RideEstimateActivity extends AppCompatActivity {

    Intent intent;
    RideNowModel model;
    double baseFare = 0;
    APIResponse asyncTask;
    Gson gson = new Gson();
    APIRequestModel apiRequestModel = new APIRequestModel();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_estimate);
        Button btnClose = ((Button)findViewById(R.id.btnCloseRideEstimate));
        intent = getIntent();
        model = (RideNowModel) intent.getSerializableExtra("rideNowDetails");

        asyncTask = new APIResponse(this, new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                try {
                    model = gson.fromJson(output, RideNowModel.class);
                    ShowRideEstimate();
                } catch (Exception ex) {
                    Log.d("Get fare estimate", ex.getMessage());
                }
            }
        }, "", "Calculating estimates");

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("rideNowDetails", model);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        List<KeyValue> keyValues = new ArrayList<KeyValue>();
        keyValues.add(new KeyValue("serializableModel", gson.toJson(model)));
        apiRequestModel.RequestUrl = getString(R.string.APIBaseURL) + getString(R.string.RideEstimate);
        apiRequestModel.KeyValuePair = keyValues;
        apiRequestModel.HttpVerb = "GET";

        asyncTask.execute(apiRequestModel);
    }
    private void ShowRideEstimate()
    {

        TextView txtViewPickupAddress = ((TextView)findViewById(R.id.txtViewPickupAddress));
        TextView txtViewTotalDistance = ((TextView)findViewById(R.id.txtViewTotalDistance));
        TextView txtViewDuration = ((TextView)findViewById(R.id.txtViewDuration));
        TextView txtViewDropAddress = ((TextView)findViewById(R.id.txtViewDropAddress));
        TextView txtViewBaseFare = ((TextView)findViewById(R.id.txtViewBaseFare));
        final TextView txtNightHaltFare = ((TextView)findViewById(R.id.txtNightHaltFare));
        TextView txtViewCouponDiscount = ((TextView)findViewById(R.id.txtCouponDiscount));
        TextView txtViewServiceTax = ((TextView)findViewById(R.id.txtSeriveTax));
        final TextView txtViewTotalFare = ((TextView)findViewById(R.id.txtTotalFare));
        final CheckBox chkRoundTrip = ((CheckBox)findViewById(R.id.chkRoundTrip));
        String[] arraySpinner = new String[] {
                "0","1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
                "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"
        };
        try
        {

            txtViewPickupAddress.setText(model.SourceLocation.Address);
            txtViewDropAddress.setText(model.DestinationLocation.Address);
            txtViewTotalDistance.setText(model.RideEstimate.TotalDistance + " km");
            txtViewDuration.setText(model.RideEstimate.Duration);
            txtViewBaseFare.setText("₹" + Double.toString(model.RideEstimate.OneWayBaseFare));
            txtNightHaltFare.setText("₹" + Double.toString(model.RideEstimate.TotalDistance));
            if(model.SelectedCoupon.CouponCode!=null) {
                txtViewCouponDiscount.setText(model.SelectedCoupon.CouponValueType.Symbol + model.SelectedCoupon.Value);
            }
            txtViewServiceTax.setText("₹" + Double.toString(model.RideEstimate.ServiceTax));
            txtViewServiceTax.setText(Double.toString(model.RideEstimate.ServiceTax)+"%");
            baseFare = model.RideEstimate.OneWayBaseFare;
            model.RideEstimate.TotalFare = Double.parseDouble(Double.toString(((baseFare) * (100+model.RideEstimate.ServiceTax) / 100)-model.SelectedCoupon.Value));
            txtViewTotalFare.setText("₹" + Double.toString(model.RideEstimate.TotalFare));

            Spinner spnrNightHalt = (Spinner) findViewById(R.id.spnrNightHalt);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, arraySpinner);
            spnrNightHalt.setAdapter(adapter);

            spnrNightHalt.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                            Object item = parent.getItemAtPosition(pos);
                            model.RideEstimate.TotalFare-= model.RideEstimate.NightHalt * model.RideEstimate.NightHaltCharges;
                            model.RideEstimate.NightHalt = Integer.parseInt(item.toString());
                            txtNightHaltFare.setText("₹" +  model.RideEstimate.NightHalt * model.RideEstimate.NightHaltCharges);
                            model.RideEstimate.TotalFare = Double.parseDouble(Double.toString((((model.RideEstimate.OneWayBaseFare + (model.RideEstimate.NightHalt * model.RideEstimate.NightHaltCharges)) * (100 + model.RideEstimate.ServiceTax) / 100) - model.SelectedCoupon.Value)));
                            txtViewTotalFare.setText("₹" + Double.toString(model.RideEstimate.TotalFare));
                        }

                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
            chkRoundTrip.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (chkRoundTrip.isChecked()) {
                        baseFare = model.RideEstimate.TwoWayBaseFare;
                    } else {
                        baseFare = model.RideEstimate.OneWayBaseFare;
                    }
                    model.RideEstimate.TotalFare = Double.parseDouble(Double.toString((((model.RideEstimate.OneWayBaseFare + (model.RideEstimate.NightHalt * model.RideEstimate.NightHaltCharges)) * (100 + model.RideEstimate.ServiceTax) / 100) - model.SelectedCoupon.Value)));
                    txtViewTotalFare.setText("₹" + Double.toString(model.RideEstimate.TotalFare));
                }
            });

        }
        catch(Exception ex)
        {
            Log.d("Ride Estimate",ex.getMessage());
        }
    }
}
