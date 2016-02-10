package Entity;

import java.io.Serializable;

/**
 * Created by akhil on 29-12-2015.
 */
public class RideNowModel implements Serializable{

    public RideNowModel()
    {
        this.SourceLocation = new LocationDetailsModel();
        this.SourceLocation.Latitude=0d;
        this.SourceLocation.Longitude=0d;
        this.DestinationLocation = new LocationDetailsModel();
        this.DestinationLocation.Latitude=0d;
        this.DestinationLocation.Longitude=0d;
        this.SelectedCoupon = new Coupon();
        this.RideEstimate = new RideEstimate();
        this.Driver = new DriverDetails();
    }

    public UserDetails userDetails;

    public String CabType;

    public String searchAddress;

    public LocationDetailsModel SourceLocation;

    public LocationDetailsModel DestinationLocation;

    public RideEstimate RideEstimate;

    public Coupon SelectedCoupon;

    public DriverDetails Driver;

    public int RideStatus;

    public String ETA;
}
