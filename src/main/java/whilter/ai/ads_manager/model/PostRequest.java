package whilter.ai.ads_manager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {
    private String content;
    private String accessToken;
    private String imageUrl;
    private String videoUrl;
    private String mediaType;
}

