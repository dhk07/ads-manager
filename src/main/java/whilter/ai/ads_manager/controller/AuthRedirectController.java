package whilter.ai.ads_manager.controller;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import whilter.ai.ads_manager.model.CustomerRegistrationDto;
import whilter.ai.ads_manager.service.CustomerService;
import whilter.ai.ads_manager.service.FacebookService;
import whilter.ai.ads_manager.utility.Utils;


@Slf4j
@Controller
@RequestMapping("/oauth2/callback")
public class AuthRedirectController {

    private final FacebookService facebookService;
    @Value("${application.facebook.callbackUrl}")
    private String facebookCallBackUrl;

    public AuthRedirectController(FacebookService facebookService, CustomerService customerService){
        this.facebookService = facebookService;
    }

    @GetMapping("/facebook")
    public String facebookCallback(
            @RequestParam("code") String code,
            Model model,
            HttpSession session) {

        log.info("in /oauth2/callback/facebook");
        try {
            Long userId = (Long) session.getAttribute("userId");
            facebookService.linkFacebookAccount(userId, code);
            model.addAttribute("message", "Facebook account linked successfully");
            CustomerRegistrationDto customerDto = Utils.customerToCustomerDto(facebookService.getByUserId(userId), facebookCallBackUrl);
//            httpSession.setAttribute("existingCustomer", customerDto);
            model.addAttribute("existingCustomer", customerDto);
            return "dashboard";
        } catch (Exception e) {
            log.error("Error linking Facebook account: {}", e.getMessage());
            model.addAttribute("Error linking Facebook account: " + e.getMessage());
            return "dashboard";
        }
    }
}
