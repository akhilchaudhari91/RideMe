package Entity;

/**
 * Created by akhil on 02-03-2016.
 */
public class Car {

    public Car()
    {
        this.CarType = new CarType();
    }

    public int Id;

    public String Model;

    public String Color;

    public String CarNumber;

    public CarType CarType;

}
