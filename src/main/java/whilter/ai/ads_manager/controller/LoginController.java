package whilter.ai.ads_manager.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import whilter.ai.ads_manager.entity.Customer;
import whilter.ai.ads_manager.model.CustomerRegistrationDto;
import whilter.ai.ads_manager.service.CustomerService;
import whilter.ai.ads_manager.utility.PasswordValidator;
import whilter.ai.ads_manager.utility.Utils;

import java.util.Optional;

@Slf4j
@Controller
public class LoginController {


    @Autowired
    private CustomerService customerService;

//    @Autowired
//    private AuthenticationManager authenticationManager;

    @RequestMapping("/")
    public String login(Authentication authentication, Model model) {
        log.info("Inside default root: {}", System.currentTimeMillis());
        return "login";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("customer", new CustomerRegistrationDto());
        return "login"; // Return the login.html Thymeleaf template
    }

    @PostMapping("/validateLogin")
    public String loginProcess(@RequestParam String userName,
                               @RequestParam String password,
                               Model model,
                               HttpSession httpSession) {
        log.info("Inside validateLogin endpoint: ");
        model.addAttribute("userName", userName);
        Optional<Customer> existingCustomer = customerService.loadUserByUsername(userName);
        if(existingCustomer.isEmpty()){
            model.addAttribute("error", "User does not exist in our system.");
            return "login";
        }
        if(!customerService.validateCustomer(password, existingCustomer.get().getPassword())){
            model.addAttribute("error", "Password is invalid.");
            return "login";
        }
        CustomerRegistrationDto customerDto = Utils.customerToCustomerDto(existingCustomer.get());
        httpSession.setAttribute("existingCustomer", customerDto);
        model.addAttribute("existingCustomer", customerDto);
        log.info("Welcome : {}", existingCustomer);
        return "dashboard";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("customer", new CustomerRegistrationDto());
        model.addAttribute("passwordValidationMessage", PasswordValidator.getPasswordValidationMessage());
        return "register";
    }

    @PostMapping("/registerCustomer")
    public String registerCustomer(@Valid @ModelAttribute("customer") CustomerRegistrationDto customerDto,
                                   BindingResult bindingResult,
                                   Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("passwordValidationMessage", PasswordValidator.getPasswordValidationMessage());
            return "register";
        }

        try {
            customerService.registerNewCustomer(customerDto);
            model.addAttribute("message", "Registration successful. Please login");
            return "login";
        } catch (Exception e) {
            model.addAttribute("registrationError", e.getMessage());
            model.addAttribute("passwordValidationMessage", PasswordValidator.getPasswordValidationMessage());
            return "register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        log.info("Inside dashboard endpoint..userName: {}", username);
        customerService.findByUsername(username).ifPresent(customer -> {
            model.addAttribute("username", customer.getUserName());
            model.addAttribute("email", customer.getEmailId());
            model.addAttribute("firstName", customer.getFirstName());
            model.addAttribute("lastName", customer.getLastName());
            model.addAttribute("authProvider", customer.getCompanyName());
        });

        return "dashboard";
    }

    @GetMapping("/change-password")
    public String changePasswordPage() {
        return "change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // Validate new password match
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "New passwords do not match");
            return "change-password";
        }

        try {
            customerService.changePassword(username, oldPassword, newPassword);
            return "redirect:/dashboard?passwordChanged=true";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "change-password";
        }
    }
}
