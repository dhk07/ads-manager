package whilter.ai.ads_manager.model;


import lombok.Data;

@Data
public class AdRequest {
    private String name;
//    private String adSetId;
    private Creative creative;
    private String status;
}
