//package whilter.ai.ads_manager.controller;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import whilter.ai.ads_manager.service.InstagramService;
//
//@RestController
//@RequestMapping("/api/instagram")
//public class InstagramController {
//
//    @Autowired
//    private InstagramService instagramService;
//
////    @PostMapping("/post")
////    public String postToInstagram(@RequestParam String caption, @RequestParam String imageUrl) {
////        return instagramService.postToInstagram(caption, imageUrl);
////    }
//
//    @GetMapping("/hello")
//    public String hello() {
//        return "Hello, Instagram!";
//    }
//    @GetMapping("/postToInstagram")
//    public String postToInstagram(@RequestParam String caption, @RequestParam String imageUrl) {
//        System.out.println("Inside postToInstagram: caption: "+caption+" -----------> imageUrl: "+imageUrl);
//        return instagramService.postToInstagram(caption, imageUrl);
//    }
//}
//
