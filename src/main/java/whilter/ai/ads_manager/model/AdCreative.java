package whilter.ai.ads_manager.model;

import lombok.Data;

@Data
public class AdCreative {
    private String name;
    private String title;
    private String body;
    private ObjectStorySpec objectStorySpec;

}
