package whilter.ai.ads_manager.controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import whilter.ai.ads_manager.model.PostRequest;
import whilter.ai.ads_manager.service.AuthenticationHandler;
import whilter.ai.ads_manager.service.ContentCreationHandler;
import whilter.ai.ads_manager.service.InstagramHandler;
import whilter.ai.ads_manager.service.PostHandler;

@Slf4j
@RestController
@RequestMapping("/instagram")
public class InstagramController {

    private final AuthenticationHandler authenticationHandler;
    private final ContentCreationHandler contentCreationHandler;
    private final PostHandler postHandler;

    public InstagramController(
            AuthenticationHandler authenticationHandler,
            ContentCreationHandler contentCreationHandler,
            PostHandler postHandler) {
        this.authenticationHandler = authenticationHandler;
        this.contentCreationHandler = contentCreationHandler;
        this.postHandler = postHandler;
    }

    @GetMapping("/post")
    public ResponseEntity<?> postToInstagram(@RequestParam String caption, @RequestParam String mediaUrl) {
        log.info("Received request: {}", caption);
        InstagramHandler chain = (req, next) ->
                authenticationHandler.handle(req,
                        (nextReq, nextHandler) ->
                                contentCreationHandler.handle(nextReq,
                                        (finalReq, finalHandler) ->
                                                postHandler.handle(finalReq, null)));

        PostRequest request = postHandler.processRequest(caption, mediaUrl);
        chain.handle(request, null);
        return ResponseEntity.ok("Post request submitted");
    }

}
