package Entity;

import com.google.api.client.util.DateTime;

import java.io.Serializable;
import java.util.List;

/**
 * Created by akhil on 31-01-2016.
 */
public class Coupon implements Serializable {
    public int Id;
    public String CouponCode ;

    public String Title ;

    public DateTime ExpiryDate ;
    public int Value;
    public boolean isSelected;
    public CouponValueType CouponValueType ;
}
