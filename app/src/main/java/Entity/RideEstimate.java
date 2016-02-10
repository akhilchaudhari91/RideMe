package Entity;

import java.io.Serializable;

/**
 * Created by akhil on 31-01-2016.
 */
public class RideEstimate implements Serializable{

    public String Duration;
    public double OneWayBaseFare;
    public double TwoWayBaseFare ;
    public double AirPortFare ;
    public double TotalDistance;
    public double OutBoundDistance;
    public int NightHalt;
    public double NightHaltCharges;
    public double ServiceTax;
    public Boolean IsRoundTrip;
    public double RoundTripFareFactor;
    public Boolean IsAirportDrop ;
    public String SourceCityName ;
    public String CouponDiscount;
    public double TotalFare;
}
