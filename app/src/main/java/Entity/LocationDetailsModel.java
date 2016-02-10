package Entity;

import java.io.Serializable;

import android.location.Location;

/**
 * Created by akhil on 29-12-2015.
 */
public class LocationDetailsModel implements Serializable{
    public Double Latitude;

    public Double Longitude;

    public String Address;

    public void SetLocationDetailsModel(Location location)
    {
        this.Latitude = location.getLatitude();
        this.Longitude = location.getLongitude();

    }
}
