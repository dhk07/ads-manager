package whilter.ai.ads_manager.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    @Autowired
    private AuthenticationManager authenticationManager;

//    @GetMapping("/login")
//    public String login(Model model) {
//        model.addAttribute("customer", new CustomerRegistrationDto());
//        return "login";
//    }

    @PostMapping("/login")
    public String loginProcess(@RequestParam String username,
                               @RequestParam String password,
                               Model model,
                               HttpServletRequest request) {
        try {
            // Attempt to authenticate the user
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, password);
            Authentication authentication = authenticationManager.authenticate(authToken);

            // If authentication is successful, set the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Redirect to dashboard
            return "redirect:/dashboard";
        } catch (BadCredentialsException e) {
            // Add error message for invalid credentials
            model.addAttribute("error", "Invalid username or password");
            return "login";
        } catch (Exception e) {
            // Handle other potential authentication errors
            model.addAttribute("error", "Authentication failed");
            return "login";
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("customer", new CustomerRegistrationDto());
        model.addAttribute("passwordValidationMessage", PasswordValidator.getPasswordValidationMessage());
        return "register";
    }

    @PostMapping("/register")
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
