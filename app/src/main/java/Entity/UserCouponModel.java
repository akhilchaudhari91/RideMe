package Entity;

/**
 * Created by akhil on 22-12-2015.
 */
public class UserCouponModel
{

    public UserCouponModel(int CouponId,String Description,String CouponCode,boolean isSelected)
    {
        this.CouponId=CouponId;
        this.CouponCode=CouponCode;
        this.Description=Description;
        this.isSelected=isSelected;
    }

    public UserCouponModel()
    {}

        public int CouponId;
        public String Description;
        public String CouponCode;
        public boolean isSelected;
    }
