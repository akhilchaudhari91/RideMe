package Entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by akhil on 02-01-2016.
 */
public class UserDetails extends User  implements Serializable {

    public List<Coupon> AvailableCoupons;
    public RideDetails RideDetails;
    public boolean IsDuplicateContact;
    public boolean IsDuplicateEmail;
}
