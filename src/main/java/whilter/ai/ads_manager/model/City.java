package whilter.ai.ads_manager.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class City {
    private String key;
    private int radius;

    @JsonProperty("distance_unit")
    private String distanceUnit;
}
