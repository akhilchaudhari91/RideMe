package Entity.GeoCoding;

import java.util.ArrayList;
import java.util.List;

public class Result {

    public List<AddressComponent> addressComponents = new ArrayList<AddressComponent>();
    public String formattedAddress;
    public Geometry geometry;
    public String placeId;
    public List<String> types = new ArrayList<String>();

}
