package Entity;

import java.text.DecimalFormat;

/**
 * Created by akhil on 18-11-2015.
 */
public class Driver {

    public Driver()
    {
        this.Car = new Car();
    }

    public int Id;

    public String FirstName ;

    public String LastName ;

    public String Email ;

    public String ContactNo ;

    public String Address ;

    public Boolean IsActive ;

    public int IsAvailable;

    public String LastLocation ;

    public Car Car ;

    public Number AverageUserRating;

    public int RatingCount;

}
