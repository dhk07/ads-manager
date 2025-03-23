package whilter.ai.ads_manager.service;

import org.springframework.stereotype.Component;
import whilter.ai.ads_manager.model.PostRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Component
public class PostHandler implements InstagramHandler {

    private final FacebookClient facebookClient;
    private final String instaBusinessIdString = "17841473206015101";
    public PostHandler(FacebookClient facebookClient) {
        this.facebookClient = facebookClient;
    }

    @Override
    public void handle(PostRequest request, InstagramHandler next) {
        String accessToken = request.getAccessToken();
        String caption = request.getContent();

        if (request.getImageUrl()==null || request.getImageUrl().isEmpty()) {
            try {
                var videoPostRequest = getStringObjectMap(request, caption, accessToken);

                Map<String, Object> response = facebookClient.createVideoPost(instaBusinessIdString, videoPostRequest);
                if (!response.containsKey("id")) {
                    throw new RuntimeException("Failed to create video post: " + response);
                }

                Map<String, Object> publishResponse = publishMedia(response, accessToken);

            } catch (Exception e) {
                System.err.println("Image Post failed: " + e.getMessage());
            }
        } else {
            try {
                Map<String, Object> imagePostRequest = new HashMap<>();
                imagePostRequest.put("image_url", request.getImageUrl());
                imagePostRequest.put("caption", caption);
                imagePostRequest.put("access_token", accessToken);

                Map<String, Object> response = facebookClient.createPost(instaBusinessIdString, imagePostRequest);
                Map<String, Object> publishResponse = publishMedia(response, accessToken);

            } catch (Exception e) {
                System.err.println("Video Post failed: " + e.getMessage());
            }
        }
    }

    private static Map<String, Object> getStringObjectMap(PostRequest request, String caption, String accessToken) {
        String videoUrl = request.getVideoUrl();
//                String response = facebookClient.createVideoPost(instaBusinessIdString, videoUrl, caption, accessToken);

        Map<String, Object> videoPostRequest = new HashMap<>();
        videoPostRequest.put("media_type", "REELS");  // Important: Explicitly set media_type
        videoPostRequest.put("video_url", videoUrl);
        videoPostRequest.put("caption", caption);
        videoPostRequest.put("access_token", accessToken);
        return videoPostRequest;
    }

    private Map<String, Object> publishMedia(Map<String, Object> response, String accessToken){
//        String publishResponse ;
        Map<String, Object> publishResponse;
        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            Map jsonMap = objectMapper.readValue(response, Map.class);
            String creationId = (String) response.get("id");
            System.out.println("Container created successfully. ContainerID: " + creationId);
            String status;
            do {
                // ✅ Check the media status
                Map<String, Object> statusResponse = facebookClient.getMediaStatus(creationId,  "Bearer " + accessToken);
                status = (String) statusResponse.get("status_code");

                if ("FINISHED".equals(status)) {
                    System.out.println("Media status: " + status);
                    break;
                }

                // ✅ Wait 5 seconds before checking again
                TimeUnit.SECONDS.sleep(5);
                System.out.println("Media status: " + status);
            } while ("IN_PROGRESS".equals(status));

            Map<String, Object> publishRequest = new HashMap<>();
            publishRequest.put("creation_id", creationId);
            publishRequest.put("access_token", accessToken);

            publishResponse = facebookClient.publishMedia(instaBusinessIdString, publishRequest);

//            publishResponse = facebookClient.publishMedia(instaBusinessIdString, creationId, accessToken);
            System.out.println("Post created successfully on instagram: " + publishResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return publishResponse;
    }

    public PostRequest processRequest(String caption, String mediaUrl) {

        PostRequest request = new PostRequest();
        request.setContent(caption);
//        if(containsImageFileName(mediaUrl))
//            request.setImageUrl(mediaUrl);
//        else if(containsVideoFileName(mediaUrl))
//            request.setVideoUrl(mediaUrl);
//        else
//            throw new IllegalArgumentException("Invalid media file type");
        request.setVideoUrl(mediaUrl);
        request.setMediaType(MediaType.VIDEO.name());
        return request;
    }

    private boolean containsImageFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }

        String lowerCaseFileName = fileName.toLowerCase();
        return lowerCaseFileName.matches(".*\\.(jpg|jpeg|png|gif|bmp|webp|tiff|svg)$");
    }
    private boolean containsVideoFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }

        String lowerCaseFileName = fileName.toLowerCase();
        return lowerCaseFileName.matches(".*\\.(mp4|mov|avi|wmv|flv|mkv|webm|m4v|3gp|ogg)$");
    }


}


