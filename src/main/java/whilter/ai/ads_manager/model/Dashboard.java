package whilter.ai.ads_manager.model;

import lombok.Data;

@Data
public class Dashboard {

    private Long userId;
    private String siteName;
    private boolean linkedStatus;
    private String callBackUrl;

    public Dashboard(String callBackUrl, boolean linkedStatus, String siteName, Long userId) {
        this.callBackUrl = callBackUrl;
        this.linkedStatus = linkedStatus;
        this.siteName = siteName;
        this.userId = userId;
    }
}
