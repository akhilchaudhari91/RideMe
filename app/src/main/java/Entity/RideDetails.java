package Entity;

import com.google.api.client.util.DateTime;

/**
 * Created by akhil on 10-02-2016.
 */
public class RideDetails {
    public DateTime UpdatedOn ;

    public Driver Driver;

    public User User ;

    public RideStatus RideStatus ;

    public Coupon SelectedCoupon;

    public String SourceLocation;

    public String SourceAddress ;

    public String DestinationLocation ;

    public String DestinationAddress ;

    public DateTime RideDate ;

    public double BaseFare ;

    public int NightHalt ;

    public boolean isRoundTrip ;

    public double TotalFare ;

    public double RideRating ;

    public String Feedback ;

    public boolean isAirportDrop ;
}
