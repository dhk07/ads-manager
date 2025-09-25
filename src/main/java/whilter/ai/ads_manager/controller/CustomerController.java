package whilter.ai.ads_manager.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import whilter.ai.ads_manager.entity.Customer;
import whilter.ai.ads_manager.model.CustomerRegistrationDto;
import whilter.ai.ads_manager.service.CustomerService;
import whilter.ai.ads_manager.utility.Utils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;
    @Value("${application.facebook.callbackUrl}")
    private String facebookCallBackUrl;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    //update customer details
    @PostMapping("/update")
    public String updateCustomerDetails(@Valid @ModelAttribute("customerDto") CustomerRegistrationDto customerDto,
                                        BindingResult bindingResult,
                                        HttpSession httpSession,
                                        Model model) {
        log.info("Inside updateCustomerDetails endpoint: ");
        try {
//            if (bindingResult.hasErrors()) {
//                model.addAttribute("customerDto", customerDto);
//                return "profile";
//            }
            customerService.updateCustomerDetails(customerDto);
            httpSession.setAttribute("existingCustomer", customerDto);
            model.addAttribute("existingCustomer", customerDto);
            log.info("Customer details updated successfully");
            return "dashboard";
        } catch (Exception e) {
            log.error("Error updating customer details: ", e);
            return "profile";
        }
    }

    @GetMapping("/profile")
    public String openEditProfilePage(Model model, HttpSession session) {
        log.info("Inside openEditProfilePage endpoint: ");
        CustomerRegistrationDto customerDto = (CustomerRegistrationDto) session.getAttribute("existingCustomer");
        model.addAttribute("customerDto", customerDto);
        return "profile";
    }

    @GetMapping("/view-details")
    public String viewDetails(@RequestParam("siteName") String siteName,
                              @RequestParam("userId") String userId,
                                Model model,
                              HttpSession httpSession){
        log.info("Inside viewDetails endpoint: {}", userId);


//        if(siteName.equalsIgnoreCase("google")){
//            Map<String, Object> googleDriveData = googleService.getDriveFiles(Long.valueOf(userId));
//            model.addAttribute("displayData", googleDriveData);
//            return "data";
//        } else if(siteName.equalsIgnoreCase("facebook")){
////            Map<String, Object> facebookData = facebookService.getFacebookUserInfo(Long.valueOf(userId));
//            Map<String, Object> facebookData = facebookService.readFacebookPosts(Long.valueOf(userId));
//
//            model.addAttribute("displayData", facebookData);
//            return "data";
//        } else if(siteName.equalsIgnoreCase("twitter")){
//
//        } else if(siteName.equalsIgnoreCase("linkedIn")){
//            List<Post> linkedinData = linkedInService.getRecentPosts(Long.valueOf(userId));
//            model.addAttribute("displayData", linkedinData);
//            return "data";
//        } else if(siteName.equalsIgnoreCase("instagram")){
//
//        } else{
//            return "campaign";
//        }
        return "campaign";
    }

}
