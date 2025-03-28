package whilter.ai.ads_manager.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import whilter.ai.ads_manager.model.CustomerRegistrationDto;
import whilter.ai.ads_manager.service.CustomerService;
import whilter.ai.ads_manager.utility.PasswordValidator;

@Slf4j
@RestController
public class LoginController {


    @Autowired
    private CustomerService customerService;

//    @Autowired
//    private AuthenticationManager authenticationManager;

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("customer", new CustomerRegistrationDto());
        return "login";
    }

    @PostMapping("/validateLogin")
    public String loginProcess(@RequestParam String userName,
                               @RequestParam String password,
                               Model model,
                               HttpSession httpSession) {
        log.info("Inside validateLogin endpoint: ");
        model.addAttribute("userName", userName);
        if(!customerService.validateCustomer(userName, password)){
            model.addAttribute("loginError", "UserName or Password invalid.");
            return "login";
        }
//        model.addAttribute("loginError", "");
        httpSession.setAttribute("userName", userName);
        log.info("Welcome : {}", userName);
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
            return "redirect:/login?registered=true";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("passwordValidationMessage", PasswordValidator.getPasswordValidationMessage());
            return "register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        customerService.findByUsername(username).ifPresent(customer -> {
            model.addAttribute("username", customer.getUserName());
            model.addAttribute("email", customer.getEmailId());
            model.addAttribute("firstName", customer.getFirstName());
            model.addAttribute("lastName", customer.getLastName());
            model.addAttribute("authProvider", customer.getProviderName());
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
