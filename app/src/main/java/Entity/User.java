package Entity;

import java.io.Serializable;

/**
 * Created by akhil on 07-11-2015.
 */
public class User implements Serializable{
    public int Id;
    public String Name;
    public String Email;
    public String ContactNo;
    public String LastLocation;
    public String Password;
    public boolean IsActive;
    public int ContactVerificationStatus;
}