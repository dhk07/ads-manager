package whilter.ai.ads_manager.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import whilter.ai.ads_manager.enums.MediaType;
import whilter.ai.ads_manager.model.PostRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class PostHandler implements InstagramHandler {

    private final FacebookClient facebookClient;
    @Value(value = "${application.facebook.insta-business-id}")
    private String instaBusinessIdString ;
    public PostHandler(FacebookClient facebookClient) {
        this.facebookClient = facebookClient;
    }

    @Override
    public String handle(PostRequest request, InstagramHandler next) {

        if (request.getVideoUrl()!=null) {
            try {
                var videoPostRequest = getStringObjectMap(request);
                Map<String, Object> response = facebookClient.createVideoPost(instaBusinessIdString, videoPostRequest);
                if (!response.containsKey("id")) {
                    throw new RuntimeException("Failed to create video post: " + response);
                }
                Map<String, Object> publishResponse = publishVideo(response, request.getAccessToken());
                if(publishResponse.get("id")!=null)
                    return "Video posted successfully on instagram. ";
                else
                    return "Failed to post video on instagram";
            } catch (Exception e) {
                log.error("Image Post failed: {}" , e.getMessage());
                return "Image Post failed: " + e.getMessage();
            }
        } else {
            try {
                Map<String, Object> imagePostRequest = new HashMap<>();
                imagePostRequest.put("image_url", request.getImageUrl());
                imagePostRequest.put("caption", request.getContent());
                imagePostRequest.put("access_token", request.getAccessToken());

                Map<String, Object> response = facebookClient.createPost(instaBusinessIdString, imagePostRequest);
                Map<String, Object> publishResponse = publishImage(response, request.getAccessToken());
                if(publishResponse.get("id")!=null)
                    return "Image posted successfully on instagram. ";
                else
                    return "Failed to post image on instagram";
            } catch (Exception e) {
                log.error("Video Post failed: {}" , e.getMessage());
                return "Video Post failed: " + e.getMessage();
            }
        }
    }

    private static Map<String, Object> getStringObjectMap(PostRequest request) {

        Map<String, Object> videoPostRequest = new HashMap<>();
        videoPostRequest.put("media_type", request.getMediaType());  // Important: Explicitly set media_type
        videoPostRequest.put("video_url", request.getVideoUrl());
        videoPostRequest.put("caption", request.getContent());
        videoPostRequest.put("access_token", request.getAccessToken());
        return videoPostRequest;
    }

    private Map<String, Object> publishImage(Map<String, Object> response, String accessToken){
        Map<String, Object> publishResponse;
        try {
            String creationId = (String) response.get("id");
            log.info("Container created successfully. ContainerID: {}" , creationId);
            Map<String, Object> publishRequest = new HashMap<>();
            publishRequest.put("creation_id", creationId);
            publishRequest.put("access_token", accessToken);

            publishResponse = facebookClient.publishMedia(instaBusinessIdString, publishRequest);
            log.info("Post created successfully on instagram: {}" , publishResponse);
        } catch (Exception e) {
            log.error("Exception while publishing image: {}" , e.getMessage());
            throw new RuntimeException(e);
        }
        return publishResponse;
    }

    private Map<String, Object> publishVideo(Map<String, Object> response, String accessToken){
        Map<String, Object> publishResponse;
        try {
            String creationId = (String) response.get("id");
            log.info("Container created for video post. ContainerID: {}" , creationId);
            String status;
            do {
                // ✅ Check the media status
                Map<String, Object> statusResponse = facebookClient.getMediaStatus(creationId,  "Bearer " +  accessToken);
                status = (String) statusResponse.get("status_code");
                log.info("Media status: {}", status);

                if ("FINISHED".equals(status)) {
                    break;
                }

                // ✅ Wait 5 seconds before checking again
                TimeUnit.SECONDS.sleep(5);
            } while ("IN_PROGRESS".equals(status));

            Map<String, Object> publishRequest = new HashMap<>();
            publishRequest.put("creation_id", creationId);
            publishRequest.put("access_token", accessToken);

            publishResponse = facebookClient.publishMedia(instaBusinessIdString, publishRequest);

//            publishResponse = facebookClient.publishMedia(instaBusinessIdString, creationId, accessToken);
            log.info("Video posted successfully on instagram: {}" , publishResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return publishResponse;
    }

    public PostRequest processRequest(String caption, String mediaUrl) {
        log.info("Inside processRequest method. mediaUrl: {} --> caption: {}" , mediaUrl, caption);
        PostRequest request = new PostRequest();
        request.setContent(caption);
        if(containsImageFileName(mediaUrl)) {
            request.setImageUrl(mediaUrl);
        }
        else {
            request.setVideoUrl(mediaUrl);
            request.setMediaType(MediaType.REELS.name());
        }
//        else
//            throw new IllegalArgumentException("Invalid media file type");

        return request;
    }

    private boolean containsImageFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }

        String lowerCaseFileName = fileName.toLowerCase();
        return lowerCaseFileName.contains("image") || lowerCaseFileName.contains("img") || lowerCaseFileName.contains("jpeg") || lowerCaseFileName.contains("png") || lowerCaseFileName.contains("jpg");
//        return lowerCaseFileName.matches(".*\\.(jpg|jpeg|png|gif|bmp|webp|tiff|svg)$");
    }
    private boolean containsVideoFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }

        String lowerCaseFileName = fileName.toLowerCase();
        return lowerCaseFileName.matches(".*\\.(mp4|mov|avi|wmv|flv|mkv|webm|m4v|3gp|ogg)$");
    }


}


