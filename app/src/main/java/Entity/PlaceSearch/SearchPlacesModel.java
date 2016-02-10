
package Entity.PlaceSearch;

import java.util.ArrayList;
import java.util.List;

public class SearchPlacesModel {

    public List<Object> htmlAttributions = new ArrayList<Object>();
    public String nextPageToken;
    public ArrayList<Result> results = new ArrayList<Result>();
    public String status;
    /*private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }*/

}
