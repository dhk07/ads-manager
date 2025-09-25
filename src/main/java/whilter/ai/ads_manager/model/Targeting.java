package whilter.ai.ads_manager.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Targeting {
    @JsonProperty("age_min")
    private int ageMin;

    @JsonProperty("age_max")
    private int ageMax;

    @JsonProperty("geo_locations")
    private GeoLocation geoLocations;

    private List<Interest> interests;
}
