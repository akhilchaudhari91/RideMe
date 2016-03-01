package Infrastructure;

/**
 * Created by akhil on 17-11-2015.
 */

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class UIHelper {

    public static CameraUpdate getUpdateCameraPosition(LatLng pos)
    {
        CameraPosition.Builder builder = CameraPosition.builder();
        builder.target(pos);
        builder.zoom(16);
        builder.bearing(45);
        builder.tilt(90);
        CameraPosition cameraPosition = builder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        return cameraUpdate;
    }

    public static LatLng GetLatLngPosition(String location)
    {
        return new LatLng(Double.parseDouble(location.split(",")[0]), Double.parseDouble(location.split(",")[1]));
    }

    public static boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isPasswordValid(String password) {
        return password.length() > 6;
    }
}
