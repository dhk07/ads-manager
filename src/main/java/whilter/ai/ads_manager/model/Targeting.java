package whilter.ai.ads_manager.model;


import lombok.Data;

import java.util.List;

@Data
public class Targeting {
    private int ageMin;
    private int ageMax;
    private List<String> interests;
    private GeoLocation geoLocation;
}
