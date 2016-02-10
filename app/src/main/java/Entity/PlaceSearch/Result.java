
package Entity.PlaceSearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Result {

    public String formatted_address;
    public Geometry geometry;
    public String icon;
    public String id;
    public String name;
    public OpeningHours openingHours;
    public String placeId;
    public Double rating;
    public String reference;
    public List<String> types = new ArrayList<String>();
    public List<Photo> photos = new ArrayList<Photo>();
    public Integer priceLevel;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
